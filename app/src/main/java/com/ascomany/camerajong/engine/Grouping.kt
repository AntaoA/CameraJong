// Defining the class of groupings


package com.ascomany.camerajong.engine

sealed class Grouping {
    abstract val tiles: List<Tile>
    abstract val isExposed: Boolean

    data class Chow(override val tiles: List<Tile.Numbered>, override val isExposed: Boolean) : Grouping() {
        init { require(tiles.size == 3) { "A Chow must have exactly 3 tiles." } }
    }

    data class Pung(override val tiles: List<Tile>, override val isExposed: Boolean) : Grouping() {
        init { require(tiles.size == 3) { "A Pung must have exactly 3 tiles." } }
    }

    data class Kong(override val tiles: List<Tile>, override val isExposed: Boolean) : Grouping() {
        init { require(tiles.size == 4) { "A Kong must have exactly 4 tiles." } }
    }

    data class Pair(override val tiles: List<Tile>) : Grouping() {
        override val isExposed = false
        init { require(tiles.size == 2) { "A Pair must have exactly 2 tiles." } }
    }
}