package com.ascomany.camerajong.engine.patterns

import com.ascomany.camerajong.engine.Grouping
import com.ascomany.camerajong.engine.Tile
import com.ascomany.camerajong.engine.isTerminalOrHonor


val pattern32 = listOf(
    Pattern(
        id = 16,
        name = "Four Pure Shifted Chows",
        description = "Four chows in one suit each shifted up 1 or 2 numbers from the last, but not a combination of both.",
        points = 32,
        excludedPatternIds = listOf(71), // Short Straight
        check = { hand ->
            val chows = hand.groupings.filterIsInstance<Grouping.Chow>()
            if (chows.size != 4) return@Pattern 0
            val firstTile = chows[0].firstTile() as Tile.Numbered
            val suit = firstTile.type
            if (!chows.all { (it.firstTile() as Tile.Numbered).type == suit }) return@Pattern 0
            val starts = chows.map { (it.firstTile() as Tile.Numbered).value }.sorted()
            val diff = starts[1] - starts[0]
            val isShifted = (diff == 1 || diff == 2) &&
                    starts[2] == starts[1] + diff &&
                    starts[3] == starts[2] + diff
            if (isShifted) 1 else 0
        }
    ),
    Pattern(
        id = 17,
        name = "Three Kongs",
        description = "A hand containing three Kongs.",
        points = 32,
        excludedPatternIds = listOf(48, 57, 67, 74), // Excludes inferior forms of Kongs
        check = { hand -> if (hand.groupings.count { it is Grouping.Kong } >= 3) 1 else 0 }
    ),
    Pattern(
        id = 18,
        name = "All Terminals and Honors",
        description = "The pair(s), Pungs or Kongs are all made up of 1 or 9 Number Tiles and Honor Tiles.",
        points = 32,
        excludedPatternIds = listOf(49, 73), // All Pungs, Pung of Terminals or Honors [cite: 610]
        check = { hand ->
            val allTiles = hand.groupings.flatMap { it.tiles }
            val allValid = allTiles.all { it.isTerminalOrHonor() }
            if (allValid) 1 else 0
        }
    )
)