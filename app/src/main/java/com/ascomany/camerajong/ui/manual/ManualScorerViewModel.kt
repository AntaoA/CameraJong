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
        val currentTiles = _groupings.value.sumOf { it.tiles.size }
        val currentKongs = _groupings.value.count { it is Grouping.Kong }

        // Calcul du nouveau maximum autorisé
        val nextIsKong = grouping is Grouping.Kong
        val maxAllowed = 14 + currentKongs + (if (nextIsKong) 1 else 0)

        if (currentTiles + grouping.tiles.size <= maxAllowed) {
            _groupings.value += grouping
            _scoreResult.value = null
        }
    }

    fun removeGrouping(index: Int) {
        val current = _groupings.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _groupings.value = current
            _scoreResult.value = null
        }
    }

    fun updateGrouping(index: Int, newGrouping: Grouping) {
        val current = _groupings.value.toMutableList()
        if (index in current.indices) {
            current[index] = newGrouping
            _groupings.value = current
            _scoreResult.value = null
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