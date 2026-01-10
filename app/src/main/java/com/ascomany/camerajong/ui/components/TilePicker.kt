package com.ascomany.camerajong.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ascomany.camerajong.engine.*

@Composable
fun TilePicker(onTileSelected: (Tile) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dots", "Bams", "Chars", "Honors")

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(350.dp)
        ) {
            when (selectedTab) {
                0 -> items(9) { i -> TileItem(Tile.Numbered(i + 1, NumberedType.DOTS)) { onTileSelected(it) } }
                1 -> items(9) { i -> TileItem(Tile.Numbered(i + 1, NumberedType.BAMBOO)) { onTileSelected(it) } }
                2 -> items(9) { i -> TileItem(Tile.Numbered(i + 1, NumberedType.CHARACTERS)) { onTileSelected(it) } }
                3 -> {
                    items(Wind.entries.toTypedArray()) { wind -> TileItem(Tile.Honor(wind)) { onTileSelected(it) } }
                    items(Dragon.entries.toTypedArray()) { dragon -> TileItem(Tile.Honor(dragon)) { onTileSelected(it) } }
                }
            }
        }
    }
}

@Composable
fun TileItem(tile: Tile, onClick: (Tile) -> Unit) {
    // Utilisation d'un OutlinedButton pour un aspect plus "touche de clavier"
    OutlinedButton(
        onClick = { onClick(tile) }, // L'action est BIEN isolée dans le onClick
        modifier = Modifier.aspectRatio(1f),
        contentPadding = PaddingValues(2.dp)
    ) {
        Text(text = getTileLabel(tile), style = MaterialTheme.typography.bodySmall)
    }
}

fun getTileLabel(tile: Tile): String = when (tile) {
    is Tile.Numbered -> "${tile.value}\n${tile.type.name.take(1)}"
    is Tile.Honor -> tile.type.toString().take(3)
    else -> "?"
}