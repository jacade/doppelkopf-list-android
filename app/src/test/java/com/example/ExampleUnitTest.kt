package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testNormalspielScoringZeroSum() {
        val gamePoints = 3
        val bockMultiplier = 1
        val totalRoundValue = gamePoints * bockMultiplier
        val winners = listOf(0, 1)

        val deltas = MutableList(4) { 0 }
        for (i in 0 until 4) {
            deltas[i] = if (winners.contains(i)) totalRoundValue else -totalRoundValue
        }

        assertEquals(listOf(3, 3, -3, -3), deltas)
        assertEquals(0, deltas.sum())
    }

    @Test
    fun testSoloistWinTriplePoints() {
        val gamePoints = 2
        val bockMultiplier = 1
        val totalRoundValue = gamePoints * bockMultiplier
        val soloistIndex = 2

        val deltas = MutableList(4) { 0 }
        for (i in 0 until 4) {
            deltas[i] = if (i == soloistIndex) totalRoundValue * 3 else -totalRoundValue
        }

        assertEquals(listOf(-2, -2, 6, -2), deltas)
        assertEquals(0, deltas.sum())
    }

    @Test
    fun testSoloistLossTripleNegativePoints() {
        val gamePoints = 2
        val bockMultiplier = 1
        val totalRoundValue = gamePoints * bockMultiplier
        val soloistIndex = 0

        val deltas = MutableList(4) { 0 }
        for (i in 0 until 4) {
            deltas[i] = if (i == soloistIndex) -totalRoundValue * 3 else totalRoundValue
        }

        assertEquals(listOf(-6, 2, 2, 2), deltas)
        assertEquals(0, deltas.sum())
    }

    @Test
    fun testNullspielZeroPoints() {
        val gamePoints = 0
        val deltas = MutableList(4) { 0 }
        assertEquals(listOf(0, 0, 0, 0), deltas)
        assertEquals(0, deltas.sum())
    }

    @Test
    fun testExponentialBockMultipliers() {
        fun calcBock(count: Int) = when (count) {
            1 -> 2
            2 -> 4
            3 -> 8
            4 -> 16
            else -> 1
        }

        assertEquals(1, calcBock(0))
        assertEquals(2, calcBock(1))
        assertEquals(4, calcBock(2))
        assertEquals(8, calcBock(3))
        assertEquals(16, calcBock(4))
    }

    @Test
    fun testBockLifecycleStartsNextGameForFourGames() {
        // Round 1: No bock active. An event triggers a Bockrunde!
        var activeBockTier = 0
        var bockGamesRemaining = 0

        // Round 1 is played at x1
        val round1Multiplier = if (activeBockTier > 0 && bockGamesRemaining > 0) 2 else 1
        assertEquals(1, round1Multiplier)

        // Event in Round 1 triggers Bockrunde for next games:
        val triggeredInRound1 = true
        if (triggeredInRound1) {
            bockGamesRemaining += 4
            activeBockTier = (activeBockTier + 1).coerceIn(1, 4)
        }

        assertEquals(4, bockGamesRemaining)
        assertEquals(1, activeBockTier)

        // Rounds 2, 3, 4, 5 are automatically doubled (x2):
        val multipliers = mutableListOf<Int>()
        for (round in 2..5) {
            val mult = if (activeBockTier > 0 && bockGamesRemaining > 0) 2 else 1
            multipliers.add(mult)
            bockGamesRemaining--
            if (bockGamesRemaining == 0) activeBockTier = 0
        }

        assertEquals(listOf(2, 2, 2, 2), multipliers)
        assertEquals(0, bockGamesRemaining)
        assertEquals(0, activeBockTier)

        // Round 6: Bockrunde is exhausted, back to x1:
        val round6Multiplier = if (activeBockTier > 0 && bockGamesRemaining > 0) 2 else 1
        assertEquals(1, round6Multiplier)
    }

    @Test
    fun testCumulativeBockMultipliersStack() {
        // Active 1 Bockrunde running with 3 games left
        var activeBockTier = 1
        var bockGamesRemaining = 3

        // Another Bockrunde is triggered!
        val triggered = true
        if (triggered) {
            bockGamesRemaining += 4
            activeBockTier = (activeBockTier + 1).coerceIn(1, 4)
        }

        assertEquals(7, bockGamesRemaining)
        assertEquals(2, activeBockTier) // Tier 2 = x4 multiplier!
    }

    @Test
    fun testDealerRotation() {
        val roundsCount = 5
        val dealers = (0 until roundsCount).map { roundIdx -> roundIdx % 4 }
        assertEquals(listOf(0, 1, 2, 3, 0), dealers)
    }
}
