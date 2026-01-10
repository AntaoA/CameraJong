// Defining the class of Hand
package com.ascomany.camerajong.engine

data class Hand(
    val concealedTiles: List<Tile>,       // The "hidden" tiles in the player's hand
    val meldGroupings: List<Grouping>,    // Combinations already placed on the table (Chow, Pung, Kong)
    val flowerTiles: List<Tile.Flower>,   // Flower tiles
    val seatWind: Wind,                   // Player Wind
)