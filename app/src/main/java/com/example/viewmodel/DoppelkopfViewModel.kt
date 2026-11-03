package com.example.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.GameSessionEntity
import com.example.data.model.GameRound
import com.example.data.model.Player
import com.example.data.repository.DoppelkopfRepository
import com.example.util.PdfReportGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlayerStats(
    val player: Player,
    val currentScore: Int,
    val gamesWon: Int,
    val gamesLost: Int,
    val totalGamesPlayed: Int,
    val soliPlayed: Int,
    val soliWon: Int,
    val rank: Int = 1
)

data class DoppelkopfUiState(
    val allSessions: List<GameSessionEntity> = emptyList(),
    val currentSession: GameSessionEntity = GameSessionEntity(),
    val players: List<Player> = listOf(
        Player(0, "Spieler 1"),
        Player(1, "Spieler 2"),
        Player(2, "Spieler 3"),
        Player(3, "Spieler 4")
    ),
    val rounds: List<GameRound> = emptyList(),
    val nextDealerIndex: Int = 0,
    val currentBockMultiplier: Int = 1,
    val stats: List<PlayerStats> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class DoppelkopfViewModel(private val repository: DoppelkopfRepository) : ViewModel() {

    private val _currentSessionId = MutableStateFlow<Long?>(null)
    private val _uiState = MutableStateFlow(DoppelkopfUiState())
    val uiState: StateFlow<DoppelkopfUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Ensure at least one session exists
            val initial = repository.getOrCreateLatestSession()
            _currentSessionId.value = initial.sessionId

            // Combine session list and active session data
            combine(
                repository.getAllSessionsFlow(),
                _currentSessionId.flatMapLatest { id ->
                    val activeId = id ?: initial.sessionId
                    combine(
                        repository.getSessionFlow(activeId),
                        repository.getRoundsFlow(activeId)
                    ) { session, rounds ->
                        Pair(session ?: initial, rounds)
                    }
                }
            ) { allSessions, (currentSession, rounds) ->
                val players = listOf(
                    Player(0, currentSession.player0Name),
                    Player(1, currentSession.player1Name),
                    Player(2, currentSession.player2Name),
                    Player(3, currentSession.player3Name)
                )

                val nextDealer = (rounds.size) % 4
                val bockMultiplier = calculateBockMultiplier(currentSession.activeBockRoundsCount)
                val stats = computePlayerStats(players, rounds)

                DoppelkopfUiState(
                    allSessions = allSessions,
                    currentSession = currentSession,
                    players = players,
                    rounds = rounds,
                    nextDealerIndex = nextDealer,
                    currentBockMultiplier = bockMultiplier,
                    stats = stats,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun calculateBockMultiplier(activeBockRoundsCount: Int): Int {
        return when (activeBockRoundsCount) {
            1 -> 2
            2 -> 4
            3 -> 8
            4 -> 16
            else -> if (activeBockRoundsCount > 4) 16 else 1
        }
    }

    private fun computePlayerStats(players: List<Player>, rounds: List<GameRound>): List<PlayerStats> {
        val scores = IntArray(4)
        val wins = IntArray(4)
        val losses = IntArray(4)
        val playedSoli = IntArray(4)
        val wonSoli = IntArray(4)

        for (round in rounds) {
            for (i in 0 until 4) {
                scores[i] = round.playerRunningScores.getOrElse(i) { scores[i] }
                val delta = round.playerScoreDeltas.getOrElse(i) { 0 }
                if (delta > 0) wins[i]++
                else if (delta < 0) losses[i]++
            }
            if (round.isSolo && round.soloistIndex != null && round.soloistIndex in 0..3) {
                val soloist = round.soloistIndex
                playedSoli[soloist]++
                if (round.winnerIndices.contains(soloist)) {
                    wonSoli[soloist]++
                }
            }
        }

        val unranked = players.map { player ->
            val i = player.id
            PlayerStats(
                player = player,
                currentScore = scores[i],
                gamesWon = wins[i],
                gamesLost = losses[i],
                totalGamesPlayed = rounds.size,
                soliPlayed = playedSoli[i],
                soliWon = wonSoli[i]
            )
        }

        val sortedByScore = unranked.sortedByDescending { it.currentScore }
        return unranked.map { item ->
            val rankIndex = sortedByScore.indexOfFirst { it.player.id == item.player.id } + 1
            item.copy(rank = rankIndex)
        }
    }

    fun selectSession(sessionId: Long) {
        _currentSessionId.value = sessionId
    }

    fun createNewSession(title: String, p0: String, p1: String, p2: String, p3: String) {
        viewModelScope.launch {
            val newId = repository.createNewSession(title, p0, p1, p2, p3)
            _currentSessionId.value = newId
        }
    }

    fun deleteCurrentSession() {
        viewModelScope.launch {
            val currentId = _uiState.value.currentSession.sessionId
            val all = _uiState.value.allSessions
            if (all.size <= 1) {
                // If only 1 session, clear rounds and rename
                val session = _uiState.value.currentSession
                repository.deleteSession(currentId)
                val fallback = repository.getOrCreateLatestSession()
                _currentSessionId.value = fallback.sessionId
            } else {
                repository.deleteSession(currentId)
                val next = all.firstOrNull { it.sessionId != currentId }
                _currentSessionId.value = next?.sessionId
            }
        }
    }

    fun updateSessionDetails(title: String, name0: String, name1: String, name2: String, name3: String) {
        viewModelScope.launch {
            val session = _uiState.value.currentSession
            repository.saveSession(
                session.copy(
                    title = title.trim().ifEmpty { session.title },
                    player0Name = name0.trim().ifEmpty { "Spieler 1" },
                    player1Name = name1.trim().ifEmpty { "Spieler 2" },
                    player2Name = name2.trim().ifEmpty { "Spieler 3" },
                    player3Name = name3.trim().ifEmpty { "Spieler 4" },
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateBockDirectly(activeCount: Int, gamesRemaining: Int) {
        viewModelScope.launch {
            val session = _uiState.value.currentSession
            repository.saveSession(
                session.copy(
                    activeBockRoundsCount = activeCount.coerceIn(0, 4),
                    bockGamesRemaining = gamesRemaining.coerceAtLeast(0),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun addBockRoundsForNextGames(roundsToAdd: Int = 1) {
        viewModelScope.launch {
            val session = _uiState.value.currentSession
            val newCount = (session.activeBockRoundsCount + roundsToAdd).coerceIn(1, 4)
            val newGames = session.bockGamesRemaining + (roundsToAdd * 4)
            repository.saveSession(
                session.copy(
                    activeBockRoundsCount = newCount,
                    bockGamesRemaining = newGames,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun submitRound(
        isSolo: Boolean,
        soloistIndex: Int? = null,
        soloType: String? = null,
        winnerIndices: List<Int>,
        gamePoints: Int,
        applyBock: Boolean,
        triggerBockForNext: Boolean = false,
        customNotes: String = ""
    ) {
        viewModelScope.launch {
            val state = _uiState.value
            val session = state.currentSession
            val previousRounds = state.rounds
            val roundNumber = previousRounds.size + 1
            val dealerIndex = (previousRounds.size) % 4

            // Current game: only multiplied if Bock is active in session AND applyBock is true
            val wasBockApplied = applyBock && session.activeBockRoundsCount > 0 && session.bockGamesRemaining > 0
            val activeBockCount = if (wasBockApplied) session.activeBockRoundsCount else 0
            val bockMultiplier = if (wasBockApplied) calculateBockMultiplier(activeBockCount) else 1
            val totalRoundValue = gamePoints * bockMultiplier

            val deltas = MutableList(4) { 0 }
            if (gamePoints > 0) {
                if (isSolo && soloistIndex != null && soloistIndex in 0..3) {
                    val soloistWon = winnerIndices.contains(soloistIndex)
                    if (soloistWon) {
                        for (i in 0 until 4) {
                            deltas[i] = if (i == soloistIndex) totalRoundValue * 3 else -totalRoundValue
                        }
                    } else {
                        for (i in 0 until 4) {
                            deltas[i] = if (i == soloistIndex) -totalRoundValue * 3 else totalRoundValue
                        }
                    }
                } else {
                    for (i in 0 until 4) {
                        deltas[i] = if (winnerIndices.contains(i)) totalRoundValue else -totalRoundValue
                    }
                }
            }

            val previousScores = previousRounds.lastOrNull()?.playerRunningScores ?: listOf(0, 0, 0, 0)
            val newRunningScores = List(4) { i ->
                previousScores.getOrElse(i) { 0 } + deltas[i]
            }

            val newRound = GameRound(
                roundNumber = roundNumber,
                dealerIndex = dealerIndex,
                isSolo = isSolo,
                soloistIndex = soloistIndex,
                soloType = soloType,
                winnerIndices = winnerIndices,
                gamePoints = gamePoints,
                activeBockCount = activeBockCount,
                bockMultiplier = bockMultiplier,
                totalRoundValue = totalRoundValue,
                playerScoreDeltas = deltas,
                playerRunningScores = newRunningScores,
                notes = customNotes,
                triggeredBockForNext = triggerBockForNext,
                timestamp = System.currentTimeMillis()
            )

            repository.insertRound(session.sessionId, newRound)

            // Update Bock queue:
            // 1. Deduct 1 game if this round used a Bock game:
            val remainingAfterThis = if (wasBockApplied) {
                (session.bockGamesRemaining - 1).coerceAtLeast(0)
            } else {
                session.bockGamesRemaining
            }

            // 2. If a new Bockrunde was triggered during this game, it takes effect from the NEXT game for 4 games:
            val (updatedActiveCount, updatedRemaining) = if (triggerBockForNext) {
                val newRemaining = remainingAfterThis + 4
                val baseTier = if (wasBockApplied && remainingAfterThis == 0) 0 else session.activeBockRoundsCount
                val newTier = (baseTier + 1).coerceIn(1, 4)
                Pair(newTier, newRemaining)
            } else {
                if (remainingAfterThis == 0) {
                    Pair(0, 0)
                } else {
                    val tier = ((remainingAfterThis + 3) / 4).coerceIn(1, session.activeBockRoundsCount.coerceAtLeast(1))
                    Pair(tier, remainingAfterThis)
                }
            }

            repository.saveSession(
                session.copy(
                    activeBockRoundsCount = updatedActiveCount,
                    bockGamesRemaining = updatedRemaining,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun undoLastRound() {
        viewModelScope.launch {
            val session = _uiState.value.currentSession
            val lastRound = _uiState.value.rounds.lastOrNull() ?: return@launch

            var remaining = session.bockGamesRemaining
            var activeCount = session.activeBockRoundsCount

            // If the undone round triggered a Bockrunde for next games, revert those 4 games:
            if (lastRound.triggeredBockForNext) {
                remaining = (remaining - 4).coerceAtLeast(0)
                activeCount = if (remaining == 0) 0 else ((remaining + 3) / 4).coerceIn(1, 4)
            }

            // If the undone round consumed a Bock game, restore that 1 game:
            if (lastRound.bockMultiplier > 1) {
                remaining += 1
                activeCount = ((remaining + 3) / 4).coerceIn(1, 4)
            }

            repository.saveSession(
                session.copy(
                    activeBockRoundsCount = activeCount,
                    bockGamesRemaining = remaining,
                    updatedAt = System.currentTimeMillis()
                )
            )

            repository.deleteLastRound(session.sessionId)
        }
    }

    fun generateExportText(): String {
        val state = _uiState.value
        val sb = StringBuilder()
        sb.appendLine("♠♥ Doppelkopf Spielbericht - ${state.currentSession.title} ♣♦")
        sb.appendLine("==========================================")
        sb.appendLine("Gespielte Runden: ${state.rounds.size}")
        sb.appendLine()
        sb.appendLine("Endstand / Rangliste:")
        state.stats.sortedBy { it.rank }.forEach { s ->
            val sign = if (s.currentScore > 0) "+" else ""
            sb.appendLine("${s.rank}. ${s.player.name}: $sign${s.currentScore} Pkt. (${s.gamesWon} Siege, ${s.gamesLost} Niederlagen, ${s.soliWon}/${s.soliPlayed} Soli)")
        }
        sb.appendLine()
        sb.appendLine("Rundenübersicht (neueste oben):")
        sb.appendLine("# | Geber | Spielwert | " + state.players.joinToString(" | ") { it.name })
        sb.appendLine("--------------------------------------------------")
        state.rounds.reversed().forEach { r ->
            val dealer = state.players.getOrNull(r.dealerIndex)?.name ?: "Sp ${r.dealerIndex + 1}"
            val bock = if (r.bockMultiplier > 1) " (x${r.bockMultiplier})" else ""
            val valStr = "${r.totalRoundValue}$bock"
            val scoresStr = r.playerRunningScores.mapIndexed { idx, score ->
                val delta = r.playerScoreDeltas.getOrElse(idx) { 0 }
                val deltaStr = if (delta >= 0) "+$delta" else "$delta"
                "$score ($deltaStr)"
            }.joinToString(" | ")
            sb.appendLine("${r.roundNumber} | $dealer | $valStr | $scoresStr")
        }
        return sb.toString()
    }

    fun generateExportCsv(): String {
        val state = _uiState.value
        val sb = StringBuilder()
        val header = listOf("Spiel", "Geber", "Spieltyp", "Basiswert", "BockMultiplikator", "Gesamtwert") +
                state.players.map { "${it.name}_Punktestand" }
        sb.appendLine(header.joinToString(";"))

        state.rounds.reversed().forEach { r ->
            val dealer = state.players.getOrNull(r.dealerIndex)?.name ?: "Sp ${r.dealerIndex + 1}"
            val row = mutableListOf(
                r.roundNumber.toString(),
                dealer,
                r.summaryTitle,
                r.gamePoints.toString(),
                r.bockMultiplier.toString(),
                r.totalRoundValue.toString()
            )
            r.playerRunningScores.forEach { row.add(it.toString()) }
            sb.appendLine(row.joinToString(";"))
        }
        return sb.toString()
    }

    fun shareExport(context: Context, asCsv: Boolean = false) {
        val content = if (asCsv) generateExportCsv() else generateExportText()
        val mimeType = if (asCsv) "text/csv" else "text/plain"
        val title = if (asCsv) "Doppelkopf_${_uiState.value.currentSession.title}.csv" else "Doppelkopf Spielstand"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_SUBJECT, "Doppelkopf Spielergebnisse - ${_uiState.value.currentSession.title}")
            type = mimeType
        }
        val shareIntent = Intent.createChooser(sendIntent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }

    fun sharePdf(context: Context) {
        val state = _uiState.value
        PdfReportGenerator.generateAndSharePdf(
            context = context,
            session = state.currentSession,
            players = state.players,
            rounds = state.rounds,
            stats = state.stats
        )
    }
}

class DoppelkopfViewModelFactory(private val repository: DoppelkopfRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoppelkopfViewModel::class.java)) {
            return DoppelkopfViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
