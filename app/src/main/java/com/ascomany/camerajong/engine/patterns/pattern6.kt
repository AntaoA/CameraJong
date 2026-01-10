package com.ascomany.camerajong.engine.patterns

import com.ascomany.camerajong.engine.Grouping
import com.ascomany.camerajong.engine.Tile

val pattern6 = listOf(
    Pattern(
        id = 49,
        name = "All Pungs",
        description = "A hand formed by four Pungs (or Kongs) and one pair.",
        points = 6,
        excludedPatternIds = listOf(73), // Pung of Terminals or Honors
        check = { hand -> if (hand.groupings.count { it.isTriplet() } == 4) 1 else 0 }
    ),
    Pattern(
        id = 50,
        name = "Half Flush",
        description = "A hand formed by tiles from any one of the three suits, in combination with Honor tiles.",
        points = 6,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val allTiles = hand.groupings.flatMap { it.tiles }
            val numbered = allTiles.filterIsInstance<Tile.Numbered>()
            val honors = allTiles.filterIsInstance<Tile.Honor>()
            if (numbered.isNotEmpty() && honors.isNotEmpty()) {
                val firstSuit = numbered[0].type
                if (numbered.all { it.type == firstSuit }) 1 else 0
            } else 0
        }
    ),
    Pattern(
        id = 51,
        name = "Mixed Shifted Chows",
        description = "Three chows, one in each suit, each shifted up one number from the last.",
        points = 6,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val chows = hand.groupings.filterIsInstance<Grouping.Chow>()
            if (chows.size < 3) return@Pattern 0
            val numbered = chows.map { it.firstTile() as Tile.Numbered }
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
        id = 52,
        name = "All Types",
        description = "A hand in which each of the five sets is composed of a different type of tile (Characters, Bamboo, Dots, Winds, and Dragons).",
        points = 6,
        excludedPatternIds = emptyList(),
        check = { hand ->
            if (hand.groupings.size != 5) return@Pattern 0
            val types = hand.groupings.map { g ->
                val t = g.firstTile()
                when {
                    t.isWind() -> "WIND"
                    t.isDragon() -> "DRAGON"
                    t is Tile.Numbered -> t.type.name
                    else -> "OTHER"
                }
            }.distinct()
            if (types.size == 5) 1 else 0
        }
    ),
    Pattern(
        id = 53,
        name = "Melded Hand",
        description = "Every set in the hand (chow, pung, kong, and pair) must be completed with tiles discarded by other players.",
        points = 6,
        excludedPatternIds = listOf(79), // Single Wait
        check = { hand ->
            if (hand.groupings.all { it.isExposed }) 1 else 0
        }
    ),
    Pattern(
        id = 54,
        name = "Two Dragon Pungs",
        description = "Two pungs (or kongs) of Dragon tiles.",
        points = 6,
        excludedPatternIds = emptyList(),
        check = { hand -> if (hand.groupings.count { it.isTriplet() && it.firstTile().isDragon() } == 2) 1 else 0 }
    )
)