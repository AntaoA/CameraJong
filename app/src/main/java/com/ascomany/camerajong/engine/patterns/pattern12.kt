package com.ascomany.camerajong.engine.patterns

import com.ascomany.camerajong.engine.Grouping
import com.ascomany.camerajong.engine.Tile

val pattern12 = listOf(
    Pattern(
        id = 34,
        name = "Lesser Honors and Knitted Tiles",
        description = "Formed by single honors, and singles of suit tiles belonging to separate Knitted sequences.",
        points = 12,
        excludedPatternIds = listOf(52, 62), // All Types, Concealed Hand
        check = { hand ->
            val knitted = hand.groupings.filterIsInstance<Grouping.KnittedHonor>().firstOrNull() ?: return@Pattern 0
            val honorCount = knitted.tiles.count { it is Tile.Honor }
            if (honorCount < 7) 1 else 0
        }
    ),
    Pattern(
        id = 35,
        name = "Knitted Straight",
        description = "A special straight formed with 3 different Knitted sequences (e.g., 1-4-7 of Dots, 2-5-8 of Characters, 3-6-9 of Bamboos).",
        points = 12,
        excludedPatternIds = emptyList(),
        check = { hand ->
            if (hand.groupings.any { it is Grouping.KnittedStraight }) 1 else 0
        }
    ),
    Pattern(
        id = 36,
        name = "Upper Five",
        description = "A hand created with suit tiles 6 through 9.",
        points = 12,
        excludedPatternIds = listOf(76), // No Honors
        check = { hand ->
            val numbered = hand.groupings.allNumbered()
            if (numbered.size == 14 && numbered.all { it.value in 6..9 }) 1 else 0
        }
    ),
    Pattern(
        id = 37,
        name = "Lower Five",
        description = "A hand created with suit tiles 1 through 4.",
        points = 12,
        excludedPatternIds = listOf(76), //  No Honors
        check = { hand ->
            val numbered = hand.groupings.allNumbered()
            if (numbered.size == 14 && numbered.all { it.value in 1..4 }) 1 else 0
        }
    ),
    Pattern(
        id = 38,
        name = "Big Three Winds",
        description = "A hand that includes one pung (or kong) of each of three different winds.",
        points = 12,
        excludedPatternIds = listOf(73), // Pung of Terminals or Honors
        check = { hand ->
            if (hand.groupings.count { it.isTriplet() && it.firstTile().isWind() } == 3) 1 else 0
        }
    )
)