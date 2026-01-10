// Utilitary functions to check the validity of groupings
package com.ascomany.camerajong.engine

fun isValidChow(tiles: List<Tile.Numbered>): Boolean {
    if (tiles.size != 3) return false

    val suit = tiles[0].type
    if (!tiles.all { it.type == suit }) return false

    val sortedValues = tiles.map { it.value }.sorted()
    return sortedValues[1] == sortedValues[0] + 1 && sortedValues[2] == sortedValues[1] + 1
}

fun areTilesIdentical(tiles: List<Tile>): Boolean {
    if (tiles.isEmpty()) return false
    val firstTile = tiles[0]
    return tiles.all { it == firstTile }
}

fun isGroupValid(grouping: Grouping): Boolean {
    return when (grouping) {
        is Grouping.Chow -> isValidChow(grouping.tiles)
        is Grouping.Pung -> areTilesIdentical(grouping.tiles) && grouping.tiles.size == 3
        is Grouping.Kong -> areTilesIdentical(grouping.tiles) && grouping.tiles.size == 4
        is Grouping.Pair -> areTilesIdentical(grouping.tiles) && grouping.tiles.size == 2
    }
}