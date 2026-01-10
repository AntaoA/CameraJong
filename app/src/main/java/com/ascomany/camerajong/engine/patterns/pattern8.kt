package com.ascomany.camerajong.engine.patterns

import com.ascomany.camerajong.engine.Grouping
import com.ascomany.camerajong.engine.Tile
import com.ascomany.camerajong.engine.NumberedType
import com.ascomany.camerajong.engine.Dragon
import com.ascomany.camerajong.engine.WinType

val pattern8 = listOf(
    Pattern(
        id = 39,
        name = "Mixed Straight",
        description = "Three chows in three suits making 9 continuous numbers (1-9).",
        points = 8,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val chows = hand.groupings.filterIsInstance<Grouping.Chow>()
            if (chows.size < 3) return@Pattern 0
            val suits = chows.map { it.tiles[0].type }.distinct()
            if (suits.size < 3) return@Pattern 0
            val starts = chows.map { it.tiles.minOf { t -> t.value } }.sorted()
            if (starts.containsAll(listOf(1, 4, 7))) 1 else 0
        }
    ),
    Pattern(
        id = 40,
        name = "Reversible Tiles",
        description = "A hand created entirely with vertically symmetrical tiles.",
        points = 8,
        excludedPatternIds = listOf(75), // One Voided Suit
        check = { hand ->
            val reversibleDots = setOf(1, 2, 3, 4, 5, 8, 9)
            val reversibleBams = setOf(2, 4, 5, 6, 8, 9)
            val allTiles = hand.groupings.flatMap { it.tiles }
            val valid = allTiles.all { t ->
                (t is Tile.Numbered && t.type == NumberedType.DOTS && t.value in reversibleDots) ||
                        (t is Tile.Numbered && t.type == NumberedType.BAMBOO && t.value in reversibleBams) ||
                        (t is Tile.Honor && t.type == Dragon.WHITE)
            }
            if (valid) 1 else 0
        }
    ),
    Pattern(
        id = 41,
        name = "Mixed Triple Chow",
        description = "Three chows of the same numerical sequence, one in each suit.",
        points = 8,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val chows = hand.groupings.filterIsInstance<Grouping.Chow>()
            val groupedByValue = chows.groupBy { it.tiles.minOf { t -> t.value } }
            val hasMixedTriple = groupedByValue.any { (_, list) ->
                list.map { it.tiles[0].type }.distinct().size == 3
            }
            if (hasMixedTriple) 1 else 0
        }
    ),
    Pattern(
        id = 42,
        name = "Mixed Shifted Pungs",
        description = "Three pungs (or kongs), one in each suit, each shifted up one number from the last.",
        points = 8,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val triplets = hand.groupings.filter { it.isTriplet() && it.firstTile() is Tile.Numbered }
            if (triplets.size < 3) return@Pattern 0
            val numbered = triplets.map { it.firstTile() as Tile.Numbered }
            val hasPattern = numbered.any { t1 ->
                numbered.any { t2 ->
                    t2.value == t1.value + 1 && t2.type != t1.type &&
                            numbered.any { t3 ->
                                t3.value == t1.value + 2 && t3.type != t1.type && t3.type != t2.type
                            }
                }
            }
            if (hasPattern) 1 else 0
        }
    ),
    Pattern(
        id = 43,
        name = "Chicken Hand",
        description = "A hand that would otherwise earn 0 points (excluding Flowers).",
        points = 8,
        excludedPatternIds = emptyList(),
        check = { hand -> 0 } // Calculated by the score engine if the total is 0
    ),
    Pattern(
        id = 44,
        name = "Last Tile Draw",
        description = "Making mahjong on a pick of the very last tile of the wall.",
        points = 8,
        excludedPatternIds = listOf(80), // Self-Drawn
        check = { hand -> if (hand.isLastTile && hand.winType == WinType.SELF_DRAWN) 1 else 0 }
    ),
    Pattern(
        id = 45,
        name = "Last Tile Claim",
        description = "Making mahjong off the discard which is the last tile in the game.",
        points = 8,
        excludedPatternIds = emptyList(),
        check = { hand -> if (hand.isLastTile && hand.winType != WinType.DISCARD) 1 else 0 }
    ),
    Pattern(
        id = 46,
        name = "Out with Replacement Tile",
        description = "Making mahjong on the replacement tile drawn after a kong.",
        points = 8,
        excludedPatternIds = emptyList(),
        check = { hand -> if (hand.winType == WinType.KONG_REPLACEMENT) 1 else 0 }
    ),
    Pattern(
        id = 47,
        name = "Robbing The Kong",
        description = "Winning off the tile that somebody adds to a melded pung.",
        points = 8,
        excludedPatternIds = listOf(58), // Last Tile
        check = { hand -> if (hand.winType == WinType.KONG_ROBBED) 1 else 0 }
    ),
    Pattern(
        id = 48,
        name = "Two Concealed Kongs",
        description = "A hand that includes two concealed kongs.",
        points = 8,
        excludedPatternIds = emptyList(),
        check = { hand -> if (hand.groupings.count { it is Grouping.Kong && !it.isExposed } == 2) 1 else 0 }
    )
)