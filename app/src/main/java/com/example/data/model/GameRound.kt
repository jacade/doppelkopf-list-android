package com.example.data.model

data class GameRound(
    val id: Long = 0L,
    val roundNumber: Int,
    val dealerIndex: Int, // 0..3
    val isSolo: Boolean,
    val soloistIndex: Int? = null,
    val soloType: String? = null,
    val winnerIndices: List<Int>, // Indices of winning players
    val gamePoints: Int, // Game base points (e.g. 0, 1, 2, 3, ...)
    val activeBockCount: Int, // 0 to 4
    val bockMultiplier: Int, // 1, 2, 4, 8, 16
    val totalRoundValue: Int, // gamePoints * bockMultiplier
    val playerScoreDeltas: List<Int>, // Size 4, points won/lost in this game
    val playerRunningScores: List<Int>, // Size 4, cumulative score after this game
    val notes: String = "",
    val triggeredBockForNext: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    val isZeroPointsGame: Boolean
        get() = gamePoints == 0

    val summaryTitle: String
        get() = when {
            isZeroPointsGame -> "Nullspiel (0 Pkt.)"
            isSolo -> {
                val soloistWon = soloistIndex != null && winnerIndices.contains(soloistIndex)
                val typeStr = soloType?.ifEmpty { "Solo" } ?: "Solo"
                if (soloistWon) "$typeStr gewonnen" else "$typeStr verloren"
            }
            else -> "Normalspiel"
        }

    val bockBadgeText: String?
        get() = if (bockMultiplier > 1) "Bock x$bockMultiplier" else null
}
