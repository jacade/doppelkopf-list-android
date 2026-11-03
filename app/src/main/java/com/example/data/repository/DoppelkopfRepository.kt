package com.example.data.repository

import com.example.data.local.DoppelkopfDao
import com.example.data.local.GameSessionEntity
import com.example.data.local.RoundEntity
import com.example.data.model.GameRound
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DoppelkopfRepository(private val dao: DoppelkopfDao) {

    fun getAllSessionsFlow(): Flow<List<GameSessionEntity>> =
        dao.getAllSessionsFlow()

    fun getSessionFlow(sessionId: Long): Flow<GameSessionEntity?> =
        dao.getSessionFlow(sessionId)

    suspend fun getSession(sessionId: Long): GameSessionEntity? =
        dao.getSession(sessionId)

    suspend fun getOrCreateLatestSession(): GameSessionEntity {
        val latest = dao.getLatestSession()
        if (latest != null) return latest

        val defaultSession = GameSessionEntity(
            title = "Hauptrunde",
            player0Name = "Spieler 1",
            player1Name = "Spieler 2",
            player2Name = "Spieler 3",
            player3Name = "Spieler 4",
            activeBockRoundsCount = 0,
            bockGamesRemaining = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val id = dao.insertSession(defaultSession)
        return defaultSession.copy(sessionId = id)
    }

    suspend fun createNewSession(
        title: String,
        p0: String,
        p1: String,
        p2: String,
        p3: String
    ): Long {
        val session = GameSessionEntity(
            title = title.trim().ifEmpty { "Neue Runde" },
            player0Name = p0.trim().ifEmpty { "Spieler 1" },
            player1Name = p1.trim().ifEmpty { "Spieler 2" },
            player2Name = p2.trim().ifEmpty { "Spieler 3" },
            player3Name = p3.trim().ifEmpty { "Spieler 4" },
            activeBockRoundsCount = 0,
            bockGamesRemaining = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return dao.insertSession(session)
    }

    suspend fun saveSession(session: GameSessionEntity) {
        dao.insertSession(session.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteSession(sessionId: Long) {
        dao.clearRoundsForSession(sessionId)
        dao.deleteSessionById(sessionId)
    }

    fun getRoundsFlow(sessionId: Long): Flow<List<GameRound>> =
        dao.getRoundsFlow(sessionId).map { list -> list.map { it.toDomain() } }

    suspend fun insertRound(sessionId: Long, round: GameRound): Long {
        val roundId = dao.insertRound(round.toEntity(sessionId))
        val session = dao.getSession(sessionId)
        if (session != null) {
            dao.insertSession(session.copy(updatedAt = System.currentTimeMillis()))
        }
        return roundId
    }

    suspend fun deleteLastRound(sessionId: Long): Boolean {
        val last = dao.getLastRound(sessionId) ?: return false
        dao.deleteRoundById(last.id)
        val session = dao.getSession(sessionId)
        if (session != null) {
            dao.insertSession(session.copy(updatedAt = System.currentTimeMillis()))
        }
        return true
    }

    private fun RoundEntity.toDomain(): GameRound {
        val winners = if (winnerIndicesCsv.isBlank()) emptyList()
        else winnerIndicesCsv.split(",").mapNotNull { it.trim().toIntOrNull() }

        val deltas = if (scoreDeltasCsv.isBlank()) listOf(0, 0, 0, 0)
        else scoreDeltasCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.let {
            if (it.size == 4) it else listOf(0, 0, 0, 0)
        }

        val running = if (runningScoresCsv.isBlank()) listOf(0, 0, 0, 0)
        else runningScoresCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.let {
            if (it.size == 4) it else listOf(0, 0, 0, 0)
        }

        return GameRound(
            id = id,
            roundNumber = roundNumber,
            dealerIndex = dealerIndex,
            isSolo = isSolo,
            soloistIndex = soloistIndex,
            soloType = soloType,
            winnerIndices = winners,
            gamePoints = gamePoints,
            activeBockCount = activeBockCount,
            bockMultiplier = bockMultiplier,
            totalRoundValue = totalRoundValue,
            playerScoreDeltas = deltas,
            playerRunningScores = running,
            notes = notes,
            triggeredBockForNext = triggeredBockForNext,
            timestamp = timestamp
        )
    }

    private fun GameRound.toEntity(sessionId: Long): RoundEntity {
        return RoundEntity(
            id = id,
            sessionId = sessionId,
            roundNumber = roundNumber,
            dealerIndex = dealerIndex,
            isSolo = isSolo,
            soloistIndex = soloistIndex,
            soloType = soloType,
            winnerIndicesCsv = winnerIndices.joinToString(","),
            gamePoints = gamePoints,
            activeBockCount = activeBockCount,
            bockMultiplier = bockMultiplier,
            totalRoundValue = totalRoundValue,
            scoreDeltasCsv = playerScoreDeltas.joinToString(","),
            runningScoresCsv = playerRunningScores.joinToString(","),
            notes = notes,
            triggeredBockForNext = triggeredBockForNext,
            timestamp = timestamp
        )
    }
}
