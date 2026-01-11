package com.ascomany.camerajong.ui.manual

import androidx.lifecycle.ViewModel
import com.ascomany.camerajong.engine.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ManualScorerViewModel(private val engine: ScoringEngine) : ViewModel() {
    // État des groupements (Paires, Chows, Pungs, Kongs)
    private val _groupings = MutableStateFlow<List<Grouping>>(emptyList())
    val groupings = _groupings.asStateFlow()

    // État de la tuile gagnante (Hu)
    private val _winningTile = MutableStateFlow<Tile?>(null)
    val winningTile = _winningTile.asStateFlow()

    // État du résultat du calcul
    private val _scoreResult = MutableStateFlow<ScoringResult?>(null)
    val scoreResult = _scoreResult.asStateFlow()

    // Paramètres de la partie
    var winType by mutableStateOf(WinType.DISCARD)
    var waitType by mutableStateOf(WaitType.SINGLE)
    var seatWind by mutableStateOf(Wind.EAST)
    var prevalentWind by mutableStateOf(Wind.EAST)


    fun addGrouping(grouping: Grouping) {
        // On ajoute directement sans vérifier les limites
        _groupings.value += grouping
        _winningTile.value = grouping.tiles.last()
        _scoreResult.value = null
    }

    fun updateGrouping(index: Int, newGrouping: Grouping) {
        val current = _groupings.value.toMutableList()
        if (index in current.indices) {
            current[index] = newGrouping
            _groupings.value = current
            _scoreResult.value = null
        }
    }

    /**
     * Supprime un groupe à un index précis.
     */
    fun removeGrouping(index: Int) {
        val current = _groupings.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _groupings.value = current
            _scoreResult.value = null
        }
    }

    /**
     * Définit manuellement la tuile qui a provoqué le Hu.
     */
    fun setWinningTile(tile: Tile) {
        _winningTile.value = tile
    }

    /**
     * Réinitialise la main complète.
     */
    fun clear() {
        _groupings.value = emptyList()
        _winningTile.value = null
        _scoreResult.value = null
    }

    // Identifie les tuiles présentes plus de 4 fois
    fun getOverLimitTiles(): Set<Tile> {
        return _groupings.value
            .flatMap { it.tiles }
            .groupBy { it }
            .filter { it.value.size > 4 }
            .keys
    }

    // Vérifie si la taille totale est correcte (14 + Kongs)
    fun isHandSizeInvalid(): Boolean {
        val totalTiles = _groupings.value.sumOf { it.tiles.size }
        val kongCount = _groupings.value.count { it is Grouping.Kong }
        return totalTiles != (14 + kongCount)
    }

    /**
     * Lance le moteur de calcul MCR.
     */
    fun calculate() {
        val currentWinningTile = _winningTile.value ?: _groupings.value.lastOrNull()?.tiles?.last()

        if (currentWinningTile != null) {
            val hand = WinningHand(
                groupings = _groupings.value,
                winningTile = currentWinningTile,
                isLastOfKind = false,
                waitType = waitType,
                winType = winType,
                seatWind = seatWind,
                prevalentWind = prevalentWind,
                flowerTiles = emptyList(), // Optionnel pour le moment
                isLastTile = false
            )
            _scoreResult.value = engine.calculateScore(hand)
        }
    }


}