package com.ascomany.camerajong.engine.patterns

import com.ascomany.camerajong.engine.Grouping
import com.ascomany.camerajong.engine.Tile
import com.ascomany.camerajong.engine.Wind
import com.ascomany.camerajong.engine.Dragon



fun List<Grouping>.countPungs() = count { it is Grouping.Pung || it is Grouping.Kong }
fun List<Grouping>.countChows() = count { it is Grouping.Chow }
fun List<Grouping>.allNumbered() = flatMap { it.tiles }.filterIsInstance<Tile.Numbered>()
fun List<Grouping>.isSingleSuit(): Boolean {
    val tiles = allNumbered()
    return if (tiles.isEmpty()) false else tiles.all { it.type == tiles[0].type }
}

fun Grouping.isTriplet(): Boolean = this is Grouping.Pung || this is Grouping.Kong
fun Grouping.firstTile(): Tile = this.tiles[0]

fun Tile.isWind(): Boolean {
    return this is Tile.Honor && this.type is Wind
}
fun Tile.isDragon(): Boolean {
    return this is Tile.Honor && this.type is Dragon
}
fun Tile.isSpecificWind(wind: Wind): Boolean {
    return this is Tile.Honor && this.type == wind
}