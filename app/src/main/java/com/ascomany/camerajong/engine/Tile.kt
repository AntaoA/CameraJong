// Defining the class of tiles

package com.ascomany.camerajong.engine

sealed class Tile {
    // Numbered tiles (3 common types: Dots, Bamboos, Characters)
    data class Numbered(val value: Int, val type: NumberedType) : Tile()

    // Honors (Winds: East, South, West, North / Dragons: Red, Green, White)
    data class Honor(val type: HonorType) : Tile()

    // Bonus (Flowers and Seasons)
    data class Flower(val value: Int, val type: FlowerType) : Tile()
}

enum class NumberedType { DOTS, BAMBOO, CHARACTERS }

sealed interface HonorType
enum class Wind : HonorType { EAST, SOUTH, WEST, NORTH }
enum class Dragon : HonorType { RED, GREEN, WHITE }

enum class FlowerType { FLOWER, SEASON }
