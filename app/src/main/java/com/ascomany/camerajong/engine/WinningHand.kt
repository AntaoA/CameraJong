package com.ascomany.camerajong.engine

data class WinningHand(
    val groupings: List<Grouping>,
    val winningTile: Tile,
    val winType: WinType,
    val seatWind: Wind,
    val prevailingWind: Wind,
    val flowerTiles: List<Tile.Flower>
)