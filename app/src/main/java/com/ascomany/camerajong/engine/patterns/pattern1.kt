package com.ascomany.camerajong.engine.patterns

import com.ascomany.camerajong.engine.Grouping
import com.ascomany.camerajong.engine.Tile
import com.ascomany.camerajong.engine.WinType
import com.ascomany.camerajong.engine.WaitType

val pattern1 = listOf(
    Pattern(
        id = 69,
        name = "Pure Double Chow",
        description = "Two identical chows in the same suit.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val chows = hand.groupings.filterIsInstance<Grouping.Chow>()
            val grouped = chows.groupBy { (it.firstTile() as Tile.Numbered).value to (it.firstTile() as Tile.Numbered).type }
            grouped.count { it.value.size == 2 }
        }
    ),
    Pattern(
        id = 70,
        name = "Mixed Double Chow",
        description = "Two chows of the same numbers but in different suits.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val chows = hand.groupings.filterIsInstance<Grouping.Chow>()
            val groupedByValue = chows.groupBy { (it.firstTile() as Tile.Numbered).value }
            val occurrences = groupedByValue.values.count { list ->
                list.size == 2 &&
                        (list[0].firstTile() as Tile.Numbered).type != (list[1].firstTile() as Tile.Numbered).type
            }
            occurrences
        }
    ),
    Pattern(
        id = 71,
        name = "Short Straight",
        description = "Two chows in the same suit that run consecutively to make a six-tile straight.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val chows = hand.groupings.filterIsInstance<Grouping.Chow>()
            val bySuit = chows.groupBy { (it.firstTile() as Tile.Numbered).type }
            bySuit.values.sumOf { suitChows ->
                val starts = suitChows.map { it.tiles.minOf { t -> t.value } }.sorted()
                starts.zipWithNext().count { it.second == it.first + 3 }
            }
        }
    ),
    Pattern(
        id = 72,
        name = "Two Terminal Chows",
        description = "Chows of 1-2-3 and 7-8-9 in the same suit.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val chows = hand.groupings.filterIsInstance<Grouping.Chow>()
            val bySuit = chows.groupBy { (it.firstTile() as Tile.Numbered).type }
            bySuit.values.count { suitChows ->
                val starts = suitChows.map { it.tiles.minOf { t -> t.value } }
                starts.contains(1) && starts.contains(7)
            }
        }
    ),
    Pattern(
        id = 73,
        name = "Pung of Terminals or Honors",
        description = "A Pung or Kong of Ones, Nines, or Winds.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand ->
            hand.groupings.count { g ->
                g.isTriplet() && g.firstTile().let { t ->
                    (t is Tile.Numbered && (t.value == 1 || t.value == 9)) || t.isWind()
                }
            }
        }
    ),
    Pattern(
        id = 74,
        name = "Melded Kong",
        description = "A kong that was claimed from another player or promoted from a melded pung.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand -> hand.groupings.count { it is Grouping.Kong && it.isExposed } }
    ),
    Pattern(
        id = 75,
        name = "One Voided Suit",
        description = "A hand that lacks any tiles from one of the three suits.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val suitsPresent = hand.groupings.flatMap { it.tiles }
                .filterIsInstance<Tile.Numbered>().map { it.type }.distinct()
            if (suitsPresent.size == 2) 1 else 0
        }
    ),
    Pattern(
        id = 76,
        name = "No Honor Tiles",
        description = "A hand formed entirely of suit tiles, without Winds or Dragons.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand ->
            val hasHonors = hand.groupings.flatMap { it.tiles }.any { it is Tile.Honor }
            if (!hasHonors) 1 else 0
        }
    ),
    Pattern(
        id = 77,
        name = "Edge Wait",
        description = "Waiting solely for a 3 to form 1-2-3, or a 7 to form 7-8-9. Only valid if no other waits.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand -> if (hand.waitType == WaitType.EDGE) 1 else 0 }
    ),
    Pattern(
        id = 78,
        name = "Closed Wait",
        description = "Waiting solely for a middle tile to form a chow (e.g., 5 to form 4-5-6).",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand -> if (hand.waitType == WaitType.CLOSED) 1 else 0 }
    ),
    Pattern(
        id = 79,
        name = "Single Wait",
        description = "Waiting solely for a tile to form the pair.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand -> if (hand.waitType == WaitType.SINGLE) 1 else 0 }
    ),
    Pattern(
        id = 80,
        name = "Self-Drawn",
        description = "Winning with a fresh tile picked from the wall.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand -> if (hand.winType == WinType.SELF_DRAWN || hand.winType == WinType.KONG_REPLACEMENT) 1 else 0 }
    ),
    Pattern(
        id = 81,
        name = "Flower Tiles",
        description = "Each Flower or Season tile awards one point.",
        points = 1,
        excludedPatternIds = emptyList(),
        check = { hand -> hand.flowerTiles.size }
    )
)