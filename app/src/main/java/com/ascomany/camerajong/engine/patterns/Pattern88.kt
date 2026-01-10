package com.ascomany.camerajong.engine.patterns

import com.ascomany.camerajong.engine.Grouping
import com.ascomany.camerajong.engine.Tile
import com.ascomany.camerajong.engine.NumberedType
import com.ascomany.camerajong.engine.Wind
import com.ascomany.camerajong.engine.Dragon

val pattern88 = listOf(
    Pattern(
        id = 1,
        name = "Big Four Winds",
        description = "Pungs or Kongs of all four Wind Tiles.",
        points = 88,
        excludedPatternIds = listOf(38, 49, 60, 61, 73),
        check = { hand ->
            if (hand.groupings.count { it.isTriplet() && it.firstTile().isWind() } == 4) 1 else 0
        }
    ),
    Pattern(
        id = 2,
        name = "Big Three Dragons",
        description = "Pungs or Kongs of all three Dragon Tiles.",
        points = 88,
        excludedPatternIds = listOf(54, 59),
        check = { hand ->
            if (hand.groupings.count { it.isTriplet() && it.firstTile().isDragon() } == 3) 1 else 0
        }
    ),
    Pattern(
        id = 3,
        name = "All Green",
        description = "A hand in which the chows, pungs and pair(s) are made up solely of \"green\" tiles: 2, 3, 4, 6, 8 Bams, and Green Dragon.",
        points = 88,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val greenValues = setOf(2, 3, 4, 6, 8)
            val allTiles = hand.groupings.flatMap { it.tiles }
            val isAllGreen = allTiles.all { t ->
                (t is Tile.Numbered && t.type == NumberedType.BAMBOO && t.value in greenValues) ||
                        (t is Tile.Honor && t.type == Dragon.GREEN)
            }
            if (isAllGreen) 1 else 0
        }
    ),
    Pattern(
        id = 4,
        name = "Nine Gates",
        description = "Holding the 1,1,1,2,3,4,5,6,7,8,9,9,9 tiles in any one of the suits, creating the nine-sided wait.",
        points = 88,
        excludedPatternIds = listOf(22, 62, 73),
        check = { hand -> if (hand.groupings.any { it is Grouping.NineGates }) 1 else 0 }
    ),
    Pattern(
        id = 5,
        name = "Four Kongs",
        description = "Any hand that includes four kongs.",
        points = 88,
        excludedPatternIds = listOf(79),
        check = { hand -> if (hand.groupings.count { it is Grouping.Kong } == 4) 1 else 0 }
    ),
    Pattern(
        id = 6,
        name = "Seven Shifted Pairs",
        description = "A hand formed by seven pairs of the same suit, each shifted one up from the last.",
        points = 88,
        excludedPatternIds = listOf(22, 62, 79),
        check = { hand ->
            val pairs = hand.groupings.filterIsInstance<Grouping.Pair>()
            if (pairs.size != 7) return@Pattern 0

            val sortedTiles = pairs.map { it.tiles[0] as Tile.Numbered }.sortedBy { it.value }
            val sameSuit = sortedTiles.all { it.type == sortedTiles[0].type }
            val isShifted = sortedTiles.zipWithNext().all { it.second.value == it.first.value + 1 }

            if (sameSuit && isShifted) 1 else 0
        }
    ),
    Pattern(
        id = 7,
        name = "Thirteen Orphans",
        description = "A hand created by singles of any 12 of the 1, 9, and Honor tiles, along with a pair of the 13th.",
        points = 88,
        excludedPatternIds = listOf(52, 62, 79),
        check = { hand -> if (hand.groupings.any { it is Grouping.ThirteenOrphans }) 1 else 0 }
    )
)