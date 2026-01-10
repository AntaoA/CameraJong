package com.ascomany.camerajong.engine.patterns

import com.ascomany.camerajong.engine.Grouping
import com.ascomany.camerajong.engine.Tile
import com.ascomany.camerajong.engine.WinType
import com.ascomany.camerajong.engine.isTerminalOrHonor


val pattern2 = listOf(
    Pattern(
        id = 59,
        name = "Dragon Pung",
        description = "A Pung or Kong of Dragon Tiles.",
        points = 2,
        excludedPatternIds = emptyList(),
        check = { hand -> hand.groupings.count { it.isTriplet() && it.firstTile().isDragon() } }
    ),
    Pattern(
        id = 60,
        name = "Prevalent Wind",
        description = "A Pung or Kong of the Wind Tile corresponding to the current Prevalent Wind.",
        points = 2,
        excludedPatternIds = emptyList(),
        check = { hand ->
            if (hand.groupings.any { it.isTriplet() && it.firstTile().isSpecificWind(hand.prevalentWind) }) 1 else 0
        }
    ),
    Pattern(
        id = 61,
        name = "Seat Wind",
        description = "A Pung or Kong of the Wind Tile corresponding to the player's Seat position.",
        points = 2,
        excludedPatternIds = emptyList(),
        check = { hand ->
            if (hand.groupings.any { it.isTriplet() && it.firstTile().isSpecificWind(hand.seatWind) }) 1 else 0
        }
    ),
    Pattern(
        id = 62,
        name = "Concealed Hand",
        description = "Having a concealed hand (no melded sets) and winning by discard.",
        points = 2,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val allConcealed = hand.groupings.all { !it.isExposed }
            if (allConcealed && hand.winType == WinType.DISCARD) 1 else 0
        }
    ),
    Pattern(
        id = 63,
        name = "All Chows",
        description = "Hand consists of all Chows and no Honors.",
        points = 2,
        excludedPatternIds = listOf(76), // No Honors (76)
        check = { hand ->
            val hasHonors = hand.groupings.flatMap { it.tiles }.any { it is Tile.Honor }
            if (!hasHonors && hand.groupings.count { it is Grouping.Chow } == 4) 1 else 0
        }
    ),
    Pattern(
        id = 64,
        name = "Tile Hog",
        description = "Using all four of a single suit tile, without using them as a Kong.",
        points = 2,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val allTiles = hand.groupings.flatMap { it.tiles }.filterIsInstance<Tile.Numbered>()
            val counts = allTiles.groupBy { it.type to it.value }.mapValues { it.value.size }
            val hogs = counts.filter { entry ->
                entry.value == 4 && hand.groupings.none { g -> g is Grouping.Kong && g.tiles[0].let { t -> t is Tile.Numbered && t.type == entry.key.first && t.value == entry.key.second } }
            }.size
            hogs
        }
    ),
    Pattern(
        id = 65,
        name = "Double Pung",
        description = "Two Pungs (or Kongs) of the same number in two different suits.",
        points = 2,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val numberedTriplets = hand.groupings.filter {
                it.isTriplet() && it.firstTile() is Tile.Numbered
            }
            val groupedByValue = numberedTriplets.groupBy {
                (it.firstTile() as Tile.Numbered).value
            }
            val occurrences = groupedByValue.values.count { list ->
                list.size == 2 &&
                        (list[0].firstTile() as Tile.Numbered).type != (list[1].firstTile() as Tile.Numbered).type
            }
            occurrences
        }
    ),
    Pattern(
        id = 66,
        name = "Two Concealed Pungs",
        description = "Two Pungs achieved without melding.",
        points = 2,
        excludedPatternIds = emptyList(),
        check = { hand -> if (hand.groupings.count { it.isTriplet() && !it.isExposed } == 2) 1 else 0 }
    ),
    Pattern(
        id = 67,
        name = "Concealed Kong",
        description = "Four identical tiles, all self-drawn, declared as a Kong.",
        points = 2,
        excludedPatternIds = emptyList(),
        check = { hand -> hand.groupings.count { it is Grouping.Kong && !it.isExposed } }
    ),
    Pattern(
        id = 68,
        name = "All Simple",
        description = "A hand formed without Terminal or Honor Tiles.",
        points = 2,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val allSimple = hand.groupings.all { g ->
                g.tiles.all { t ->
                    !t.isTerminalOrHonor() // Ajout de l'opérateur de négation '!'
                }
            }
            if (allSimple) 1 else 0
        }
    )
)