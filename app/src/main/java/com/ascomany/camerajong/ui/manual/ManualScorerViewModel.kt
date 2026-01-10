package com.ascomany.camerajong.ui.manual

import androidx.lifecycle.ViewModel
import com.ascomany.camerajong.engine.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ManualScorerViewModel(private val engine: ScoringEngine) : ViewModel() {
    private val _groupings = MutableStateFlow<List<Grouping>>(emptyList())
    val groupings = _groupings.asStateFlow()

    private val _scoreResult = MutableStateFlow<ScoringResult?>(null)
    val scoreResult = _scoreResult.asStateFlow()

    var winType = WinType.DISCARD
    var waitType = WaitType.SINGLE
    var seatWind = Wind.EAST
    var prevalentWind = Wind.EAST

    fun addGrouping(grouping: Grouping) {
        if (_groupings.value.sumOf { it.tiles.size } + grouping.tiles.size <= 14) {
            _groupings.value += grouping
        }
    }

    fun clear() {
        _groupings.value = emptyList()
        _scoreResult.value = null
    }

    fun calculate(winningTile: Tile) {
        val hand = WinningHand(
            groupings = _groupings.value,
            winningTile = winningTile,
            isLastOfKind = false,
            waitType = waitType,
            winType = winType,
            seatWind = seatWind,
            prevalentWind = prevalentWind,
            flowerTiles = emptyList(),
            isLastTile = false
        )
        _scoreResult.value = engine.calculateScore(hand)
    }
}