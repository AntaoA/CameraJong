package com.ascomany.camerajong.ui.manual

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
    val winningTile by viewModel.winningTile.collectAsState()
    val overLimitTiles = remember(groupings) { viewModel.getOverLimitTiles() }
    val isSizeInvalid = remember(groupings) { viewModel.isHandSizeInvalid() }

    var showTilePicker by remember { mutableStateOf(false) }
    var pendingGroupType by remember { mutableStateOf<String?>(null) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    // Calcul de la validité : 14 tuiles + 1 par Kong
    val totalTiles = groupings.sumOf { it.tiles.size }
    val kongCount = groupings.count { it is Grouping.Kong }
    val isValidHandSize = totalTiles == (14 + kongCount)

    Scaffold(
        topBar = { TopAppBar(title = { Text("MCR Scorer") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            item {
                Text(
                    text = "Main ($totalTiles/${14 + kongCount} tuiles)",
                    color = if (isSizeInvalid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Text("Clic sur une tuile = Hu | Appui long = Modifier", style = MaterialTheme.typography.labelSmall)

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(groupings) { index, group ->
                        GroupCard(
                            group = group,
                            selectedWinningTile = winningTile,
                            overLimitTiles = overLimitTiles, // <--- Passez la variable ici
                            onTileClick = { viewModel.setWinningTile(it) },
                            onLongClick = { editingIndex = index }
                        )
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
                    onClick = { viewModel.calculate() },
                    enabled = isValidHandSize,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("CALCULER LE SCORE") }
            }

            item {
                result?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
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

    // Menu Modification / Suppression
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupCard(
    group: Grouping,
    selectedWinningTile: Tile?,
    overLimitTiles: Set<Tile>,
    onTileClick: (Tile) -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier.combinedClickable(
            onClick = { /* ... */ },
            onLongClick = onLongClick
        ),
        // Bordure rouge si le groupe contient une erreur
        border = if (group.tiles.any { it in overLimitTiles })
            BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (group.isExposed) "EXP" else "HID", style = MaterialTheme.typography.labelSmall)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                group.tiles.forEach { tile ->
                    val isWinning = tile == selectedWinningTile
                    val isOverLimit = tile in overLimitTiles // Vérification d'erreur

                    Surface(
                        onClick = { onTileClick(tile) },
                        color = when {
                            tile in overLimitTiles -> MaterialTheme.colorScheme.error // Rouge si > 4 exemplaires
                            tile == selectedWinningTile -> MaterialTheme.colorScheme.errorContainer // Bleu/Rose si gagnante
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = MaterialTheme.shapes.extraSmall,
                        border = if (isWinning) BorderStroke(2.dp, MaterialTheme.colorScheme.error) else null,
                        modifier = Modifier.size(32.dp, 44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = getTileLabel(tile),
                                style = MaterialTheme.typography.labelSmall,
                                // Texte blanc si la tuile est rouge d'erreur
                                color = if (isOverLimit) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Text(group.javaClass.simpleName.replace("Grouping$", ""), style = MaterialTheme.typography.labelSmall)
        }
    }
}


@Composable
fun <T : Enum<T>> EnumSelector(label: String, values: Array<T>, selected: T, onSelect: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
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
fun GroupCreationDialog(type: String, onDismiss: () -> Unit, onConfirm: (Tile, Boolean) -> Unit) {
    var isExposed by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter $type") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isExposed, onCheckedChange = { isExposed = it })
                    Text("Groupe exposé (Mélange / Discard)")
                }
                TilePicker { onConfirm(it, isExposed) }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}