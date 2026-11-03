package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0L,
    val title: String = "Spielrunde",
    val player0Name: String = "Spieler 1",
    val player1Name: String = "Spieler 2",
    val player2Name: String = "Spieler 3",
    val player3Name: String = "Spieler 4",
    val activeBockRoundsCount: Int = 0, // Concurrent active Bockrunden: 0..4 (x2, x4, x8, x16)
    val bockGamesRemaining: Int = 0, // Remaining games at current active Bock level
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_rounds")
data class RoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sessionId: Long,
    val roundNumber: Int,
    val dealerIndex: Int,
    val isSolo: Boolean,
    val soloistIndex: Int?,
    val soloType: String?,
    val winnerIndicesCsv: String, // e.g. "0,2"
    val gamePoints: Int,
    val activeBockCount: Int,
    val bockMultiplier: Int,
    val totalRoundValue: Int,
    val scoreDeltasCsv: String, // e.g. "3,-3,3,-3"
    val runningScoresCsv: String, // e.g. "3,-3,3,-3"
    val notes: String,
    val triggeredBockForNext: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
