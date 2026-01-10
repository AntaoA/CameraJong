// Defining the class of groupings
package com.ascomany.camerajong.engine

sealed class Grouping {
    abstract val tiles: List<Tile>
    abstract val isExposed: Boolean

    // Standard structures
    data class Chow(override val tiles: List<Tile.Numbered>, override val isExposed: Boolean) :
        Grouping() {
        init {
            require(isValidChow(tiles)) { "Invalid Chow: tiles must be 3 consecutive of the same suit." }
        }
    }

    data class Pung(override val tiles: List<Tile>, override val isExposed: Boolean) : Grouping() {
        init {
            require(tiles.size == 3) { "A Pung must have exactly 3 tiles." }
            require(areTilesIdentical(tiles)) { "A Pung must consist of 3 identical tiles." }
        }
    }

    data class Kong(override val tiles: List<Tile>, override val isExposed: Boolean) : Grouping() {
        init {
            require(tiles.size == 4) { "A Kong must have exactly 4 tiles." }
            require(areTilesIdentical(tiles)) { "A Kong must consist of 4 identical tiles." }
        }
    }

    data class Pair(override val tiles: List<Tile>, override val isExposed: Boolean) : Grouping() {
        init {
            require(tiles.size == 2) { "A Pair must have exactly 2 tiles." }
            require(areTilesIdentical(tiles)) { "A Pair must consist of 2 identical tiles." }
        }
    }

    // Special structures
    data class ThirteenOrphans(override val tiles: List<Tile>) : Grouping() {
        override val isExposed = false
        init {
            require(tiles.size == 14) { "Thirteen Orphans must have 14 tiles." }
            val uniqueTiles = tiles.distinct()
            require(uniqueTiles.size == 13) { "Thirteen Orphans must have exactly 13 unique tiles." }
            require(uniqueTiles.all { it.isTerminalOrHonor() }) { "All tiles must be Terminals or Honors." }
            val hasPair = tiles.groupBy { it }.any { it.value.size == 2 }
            require(hasPair) { "Thirteen Orphans must contain one pair among the 13 terminal/honor tiles." }
        }
    }

    data class NineGates(override val tiles: List<Tile>) : Grouping() {
        override val isExposed = false
        init {
            require(tiles.size == 14) { "Nine Gates must have 14 tiles." }
            val numbered = tiles.filterIsInstance<Tile.Numbered>()
            require(numbered.size == 14 && numbered.all { it.type == numbered[0].type }) { "Nine Gates must be a single suit." }
            val counts = numbered.groupBy { it.value }.mapValues { it.value.size }
            require(counts[1]!! >= 3 && counts[9]!! >= 3 && (2..8).all { counts[it]!! >= 1 }) { "Invalid Nine Gates structure." }
        }
    }

    // Only knitted tiles (at least 7) and unique honors
    data class KnittedHonor(override val tiles: List<Tile>) : Grouping() {
        override val isExposed = false
        init {
            require(tiles.size == 14) { "Knitted Honor hand must have 14 tiles." }
            require(tiles.distinct().size == 14) { "All tiles must be unique (no pairs allowed)." }
            val numbered = tiles.filterIsInstance<Tile.Numbered>()
            require(numbered.size >= 7) { "At least 7 numbered tiles are required." }
            numbered.groupBy { it.type }.forEach { (_, suitTiles) ->
                val residues = suitTiles.map { it.value % 3 }.distinct()
                require(residues.size == 1) { "Suit tiles must belong to the same sequence (1-4-7 or 2-5-8 or 3-6-9)." }
            }
        }
    }

    data class KnittedStraight(override val tiles: List<Tile.Numbered>) : Grouping() {
        override val isExposed = false
        init {
            require(tiles.size == 9) { "Knitted Straight grouping must have exactly 9 tiles." }
            val bySuit = tiles.groupBy { it.type }
            require(bySuit.size == 3) { "Knitted Straight must include all three suits." }
            val sequences = mutableSetOf<Int>()
            bySuit.forEach { (_, suitTiles) ->
                require(suitTiles.size == 3) { "Each suit in a Knitted Straight must have 3 tiles." }
                val sorted = suitTiles.sortedBy { it.value }
                require(sorted[1].value == sorted[0].value + 3 && sorted[2].value == sorted[1].value + 3) { "Tiles in each suit must follow a knitted sequence (gap of 3)." }
                sequences.add(sorted[0].value % 3)
            }
            require(sequences.size == 3) { "Each of the 3 suits must belong to a different sequence (1-4-7, 2-5-8, and 3-6-9)." }
        }
    }
}