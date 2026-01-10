package com.ascomany.camerajong.ui.manual

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ascomany.camerajong.engine.*
import com.ascomany.camerajong.ui.components.TilePicker
import com.ascomany.camerajong.ui.components.getTileLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualScorerScreen(viewModel: ManualScorerViewModel) {
    val groupings by viewModel.groupings.collectAsState()
    val result by viewModel.scoreResult.collectAsState()

    var showTilePicker by remember { mutableStateOf(false) }
    var pendingGroupType by remember { mutableStateOf<String?>(null) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    val totalTiles = groupings.sumOf { it.tiles.size }
    val kongCount = groupings.count { it is Grouping.Kong }
    val isValidHandSize = totalTiles == (14 + kongCount)

    Scaffold(
        topBar = { TopAppBar(title = { Text("MCR Scorer") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                Text("Main ($totalTiles/${14 + kongCount} tuiles)")
                LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    // Utilisation de itemsIndexed pour gérer l'index au clic
                    itemsIndexed(groupings) { index, group ->
                        GroupCard(group = group, onClick = { editingIndex = index })
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { pendingGroupType = "Pair"; showTilePicker = true }) { Text("Paire") }
                    Button(onClick = { pendingGroupType = "Chow"; showTilePicker = true }) { Text("Chow") }
                    Button(onClick = { pendingGroupType = "Pung"; showTilePicker = true }) { Text("Pung") }
                    Button(onClick = { pendingGroupType = "Kong"; showTilePicker = true }) { Text("Kong") }
                }

                TextButton(
                    onClick = { viewModel.clear() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Réinitialiser") }
            }

            item { HorizontalDivider(Modifier.padding(vertical = 16.dp)) }

            item {
                EnumSelector("Win", WinType.entries.toTypedArray(), viewModel.winType) { viewModel.winType = it }
                EnumSelector("Wait", WaitType.entries.toTypedArray(), viewModel.waitType) { viewModel.waitType = it }
                EnumSelector("Seat", Wind.entries.toTypedArray(), viewModel.seatWind) { viewModel.seatWind = it }
                EnumSelector("Prevalent", Wind.entries.toTypedArray(), viewModel.prevalentWind) { viewModel.prevalentWind = it }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val winningTile = groupings.lastOrNull()?.tiles?.last()
                        if (winningTile != null) viewModel.calculate(winningTile)
                    },
                    enabled = isValidHandSize,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("CALCULER LE SCORE") }
            }

            item {
                result?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Total: ${it.totalPoints} pts", style = MaterialTheme.typography.headlineMedium)
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            it.detailedPatterns.forEach { p ->
                                Text("${p.name}: ${p.points} pts x${p.count}")
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogue d'options (Modifier/Supprimer) au clic sur un groupe
    if (editingIndex != null && !showTilePicker) {
        val index = editingIndex!!
        AlertDialog(
            onDismissRequest = { editingIndex = null },
            title = { Text("Modifier le groupe") },
            text = { Text("Voulez-vous supprimer ce groupe ou le remplacer ?") },
            confirmButton = {
                TextButton(onClick = {
                    pendingGroupType = when(groupings[index]) {
                        is Grouping.Pair -> "Pair"
                        is Grouping.Chow -> "Chow"
                        is Grouping.Kong -> "Kong"
                        else -> "Pung"
                    }
                    showTilePicker = true
                }) { Text("Remplacer") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.removeGrouping(index)
                        editingIndex = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Supprimer") }
            }
        )
    }

    if (showTilePicker) {
        GroupCreationDialog(
            type = pendingGroupType ?: "Pung",
            onDismiss = {
                showTilePicker = false
                editingIndex = null
            },
            onConfirm = { tile, isExposed ->
                val group = when (pendingGroupType) {
                    "Pair" -> Grouping.Pair(listOf(tile, tile), isExposed)
                    "Chow" -> {
                        val first = tile as Tile.Numbered
                        Grouping.Chow(listOf(first, first.copy(value = first.value + 1), first.copy(first.value + 2)), isExposed)
                    }
                    "Kong" -> Grouping.Kong(listOf(tile, tile, tile, tile), isExposed)
                    else -> Grouping.Pung(listOf(tile, tile, tile), isExposed)
                }

                if (editingIndex != null) {
                    viewModel.updateGrouping(editingIndex!!, group)
                    editingIndex = null
                } else {
                    viewModel.addGrouping(group)
                }
                showTilePicker = false
            }
        )
    }
}

@Composable
fun GroupCard(group: Grouping, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (group.isExposed) "EXP" else "HID", style = MaterialTheme.typography.labelSmall)
            Text(getTileLabel(group.tiles.first()))
            Text(group.javaClass.simpleName.replace("Grouping$", ""), style = MaterialTheme.typography.labelSmall)
        }
    }
}
@Composable
fun <T : Enum<T>> EnumSelector(label: String, values: Array<T>, selected: T, onSelect: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("$label: ${selected.name}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            values.forEach { value ->
                DropdownMenuItem(
                    text = { Text(value.name) },
                    onClick = { onSelect(value); expanded = false }
                )
            }
        }
    }
}

@Composable
fun GroupCard(group: Grouping) {
    Card(modifier = Modifier.padding(4.dp)) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (group.isExposed) "EXP" else "HID", style = MaterialTheme.typography.labelSmall)
            Text(getTileLabel(group.tiles.first()))
            Text(group.javaClass.simpleName, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun GroupCreationDialog(type: String, onDismiss: () -> Unit, onConfirm: (Tile, Boolean) -> Unit) {
    var isExposed by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter $type") },
        text = {
            Column {
                if (type != "Pair") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isExposed, onCheckedChange = { isExposed = it })
                        Text("Groupe exposé (Mélange)")
                    }
                }
                TilePicker { selectedTile -> onConfirm(selectedTile, isExposed) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}