package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Player
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ScoreNegative
import com.example.ui.theme.ScorePositive
import com.example.ui.theme.ScoreZero
import com.example.ui.theme.TableFelt
import com.example.ui.theme.TableFeltCard
import com.example.ui.theme.TableFeltElevated

@Composable
fun AddRoundDialog(
    roundNumber: Int,
    dealerPlayer: Player,
    players: List<Player>,
    activeBockRoundsCount: Int,
    currentBockMultiplier: Int,
    bockGamesRemaining: Int,
    onDismiss: () -> Unit,
    onSubmitRound: (
        isSolo: Boolean,
        soloistIndex: Int?,
        soloType: String?,
        winnerIndices: List<Int>,
        gamePoints: Int,
        applyBock: Boolean,
        triggerBockForNext: Boolean,
        notes: String
    ) -> Unit
) {
    // Tab State: 0 = Schnellmodus (Default), 1 = Detail-Rechner
    var selectedTab by remember { mutableIntStateOf(0) }

    // Shared States
    var isSolo by remember { mutableStateOf(false) }
    var soloistIndex by remember { mutableStateOf<Int?>(0) }
    var soloistWon by remember { mutableStateOf(true) }
    var soloType by remember { mutableStateOf("Farbsolo") }

    // Normalspiel selected winners (2 players)
    val selectedWinners = remember { mutableStateListOf(0, 1) }

    // Points entered
    var gamePoints by remember { mutableIntStateOf(1) }

    // Is a Bockrunde actively running for THIS round? (Starts only from game 2 if game 1 triggered it)
    val hasActiveBockForThisRound = activeBockRoundsCount > 0 && bockGamesRemaining > 0
    var applyBock by remember { mutableStateOf(hasActiveBockForThisRound) }

    // Does an event in this round trigger a Bockrunde for the NEXT 4 games?
    var triggerBockForNext by remember { mutableStateOf(false) }

    // Detail Rechner state
    var rePlayer1 by remember { mutableIntStateOf(0) }
    var rePlayer2 by remember { mutableIntStateOf(1) }
    var reWon by remember { mutableStateOf(true) }
    var ansageRe by remember { mutableStateOf(false) }
    var ansageKontra by remember { mutableStateOf(false) }
    var keine90 by remember { mutableStateOf(false) }
    var keine60 by remember { mutableStateOf(false) }
    var keine30 by remember { mutableStateOf(false) }
    var schwarz by remember { mutableStateOf(false) }
    var gegenDieAlten by remember { mutableStateOf(false) }
    var fuechseGefangen by remember { mutableIntStateOf(0) }
    var karlchen by remember { mutableStateOf(false) }
    var doppelkopfCount by remember { mutableIntStateOf(0) }

    // Computed effective multiplier for THIS round
    val effectiveMultiplier = if (applyBock && hasActiveBockForThisRound) {
        currentBockMultiplier
    } else 1

    // Detailed points calculation when in Tab 1
    val calculatedDetailedPoints by remember {
        derivedStateOf {
            if (isSolo) {
                gamePoints
            } else {
                var sum = 1 // Basis gewonnen
                if (ansageRe) sum += 2
                if (ansageKontra) sum += 2
                if (keine90) sum += 1
                if (keine60) sum += 1
                if (keine30) sum += 1
                if (schwarz) sum += 1
                if (gegenDieAlten) sum += 1
                sum += fuechseGefangen
                if (karlchen) sum += 1
                sum += doppelkopfCount
                sum
            }
        }
    }

    val activePoints = if (selectedTab == 1 && !isSolo) calculatedDetailedPoints else gamePoints
    val totalRoundValue = activePoints * effectiveMultiplier

    // Computed Winners list
    val currentWinners: List<Int> = if (selectedTab == 1 && !isSolo) {
        if (reWon) listOf(rePlayer1, rePlayer2)
        else (0..3).filter { it != rePlayer1 && it != rePlayer2 }
    } else if (isSolo) {
        val s = soloistIndex ?: 0
        if (soloistWon) listOf(s) else (0..3).filter { it != s }
    } else {
        selectedWinners.toList()
    }

    // Preview Deltas
    val previewDeltas = remember(activePoints, effectiveMultiplier, isSolo, soloistIndex, soloistWon, currentWinners) {
        val deltas = MutableList(4) { 0 }
        if (activePoints > 0) {
            if (isSolo && soloistIndex != null) {
                val s = soloistIndex!!
                if (soloistWon) {
                    for (i in 0 until 4) deltas[i] = if (i == s) totalRoundValue * 3 else -totalRoundValue
                } else {
                    for (i in 0 until 4) deltas[i] = if (i == s) -totalRoundValue * 3 else totalRoundValue
                }
            } else {
                for (i in 0 until 4) {
                    deltas[i] = if (currentWinners.contains(i)) totalRoundValue else -totalRoundValue
                }
            }
        }
        deltas
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .clip(RoundedCornerShape(16.dp))
                .testTag("add_round_dialog"),
            color = TableFeltCard,
            border = BorderStroke(1.5.dp, GoldPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header: Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Spiel #$roundNumber werten",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldLight
                            )
                        }
                        Text(
                            text = "Geber: ${dealerPlayer.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen",
                            tint = Color(0xFFA1A1AA)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs: Schnellmodus vs Detail-Rechner
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = TableFeltElevated,
                    contentColor = GoldPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldPrimary,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "⚡ Schnellmodus",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) GoldLight else Color(0xFFA1A1AA)
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "🧮 Detail-Rechner",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) GoldLight else Color(0xFFA1A1AA)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Spielart-Umschalter: Normalspiel vs. Solo (in both tabs)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (!isSolo) GoldPrimary else TableFeltElevated,
                        border = BorderStroke(1.dp, if (!isSolo) GoldPrimary else Color(0x33D4AF37)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isSolo = false }
                            .testTag("mode_normal_button")
                    ) {
                        Text(
                            text = "Normalspiel (2 vs 2)",
                            fontWeight = if (!isSolo) FontWeight.Bold else FontWeight.Normal,
                            color = if (!isSolo) Color(0xFF091F13) else Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp),
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSolo) GoldPrimary else TableFeltElevated,
                        border = BorderStroke(1.dp, if (isSolo) GoldPrimary else Color(0x33D4AF37)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isSolo = true }
                            .testTag("mode_solo_button")
                    ) {
                        Text(
                            text = "Solo (x3 Wertung)",
                            fontWeight = if (isSolo) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSolo) Color(0xFF091F13) else Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // CONTENT PER MODE
                if (selectedTab == 0) {
                    // ================= SCHNELLMODUS =================
                    QuickModeContent(
                        isSolo = isSolo,
                        players = players,
                        selectedWinners = selectedWinners,
                        soloistIndex = soloistIndex,
                        soloistWon = soloistWon,
                        soloType = soloType,
                        gamePoints = gamePoints,
                        onWinnersChange = { w1, w2 ->
                            selectedWinners.clear()
                            selectedWinners.add(w1)
                            selectedWinners.add(w2)
                        },
                        onToggleWinner = { idx ->
                            if (selectedWinners.contains(idx)) {
                                if (selectedWinners.size > 1) selectedWinners.remove(idx)
                            } else {
                                if (selectedWinners.size >= 2) selectedWinners.removeAt(0)
                                selectedWinners.add(idx)
                            }
                        },
                        onSoloistChange = { soloistIndex = it },
                        onSoloistWonChange = { soloistWon = it },
                        onSoloTypeChange = { soloType = it },
                        onPointsChange = { gamePoints = it }
                    )
                } else {
                    // ================= DETAIL-RECHNER =================
                    DetailedModeContent(
                        isSolo = isSolo,
                        players = players,
                        soloistIndex = soloistIndex,
                        soloistWon = soloistWon,
                        soloType = soloType,
                        gamePoints = gamePoints,
                        rePlayer1 = rePlayer1,
                        rePlayer2 = rePlayer2,
                        reWon = reWon,
                        ansageRe = ansageRe,
                        ansageKontra = ansageKontra,
                        keine90 = keine90,
                        keine60 = keine60,
                        keine30 = keine30,
                        schwarz = schwarz,
                        gegenDieAlten = gegenDieAlten,
                        fuechseGefangen = fuechseGefangen,
                        karlchen = karlchen,
                        doppelkopfCount = doppelkopfCount,
                        onRePlayer1Change = { rePlayer1 = it },
                        onRePlayer2Change = { rePlayer2 = it },
                        onReWonChange = { reWon = it },
                        onAnsageReChange = { ansageRe = it },
                        onAnsageKontraChange = { ansageKontra = it },
                        onKeine90Change = { keine90 = it },
                        onKeine60Change = { keine60 = it },
                        onKeine30Change = { keine30 = it },
                        onSchwarzChange = { schwarz = it },
                        onGegenDieAltenChange = { gegenDieAlten = it },
                        onFuechseChange = { fuechseGefangen = it },
                        onKarlchenChange = { karlchen = it },
                        onDoppelkopfChange = { doppelkopfCount = it },
                        onPointsChange = { gamePoints = it },
                        onSoloistChange = { soloistIndex = it },
                        onSoloistWonChange = { soloistWon = it },
                        onSoloTypeChange = { soloType = it }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 1. ACTIVE BOCKRUNDE FOR THIS GAME (if active, automatically applied)
                if (hasActiveBockForThisRound) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (applyBock) AmberAccent.copy(alpha = 0.18f) else TableFeltElevated,
                        border = BorderStroke(1.dp, if (applyBock) AmberAccent else Color(0x33FFFFFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { applyBock = !applyBock }
                            .testTag("apply_bock_checkbox_row")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = applyBock,
                                onCheckedChange = { applyBock = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = AmberAccent,
                                    checkmarkColor = Color(0xFF1E0E05)
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = if (applyBock) AmberAccent else Color(0xFFA1A1AA),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Bockrunde aktiv für Spiel #$roundNumber (x$currentBockMultiplier)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (applyBock) AmberAccent else Color.White
                                )
                                Text(
                                    text = "Wird automatisch angerechnet • noch $bockGamesRemaining Spiele",
                                    fontSize = 11.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 2. TRIGGER NEW BOCKRUNDE FOR NEXT ROUND (e.g. 120:120, lost solo, heart trick)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (triggerBockForNext) Color(0x33E5A020) else TableFeltElevated,
                    border = BorderStroke(1.dp, if (triggerBockForNext) AmberAccent else Color(0x33FFFFFF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { triggerBockForNext = !triggerBockForNext }
                        .testTag("trigger_bock_for_next_row")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = triggerBockForNext,
                            onCheckedChange = { triggerBockForNext = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AmberAccent,
                                checkmarkColor = Color(0xFF1E0E05)
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = if (triggerBockForNext) AmberAccent else Color(0xFFA1A1AA),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Bockrunde ausgelöst (+4 Spiele ab nächstem Spiel)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (triggerBockForNext) AmberAccent else Color.White
                            )
                            Text(
                                text = "Gilt erst ab Spiel #${roundNumber + 1} (kumulativ x2, x4, x8, x16)",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // LIVE PREVIEW CARD
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = TableFeltElevated),
                    border = BorderStroke(1.dp, Color(0x33D4AF37)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Punkte-Vorschau:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            Text(
                                text = "Spielwert: $totalRoundValue Pkt." + (if (effectiveMultiplier > 1) " (Bock x$effectiveMultiplier)" else ""),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberAccent
                            )
                        }

                        if (triggerBockForNext) {
                            Text(
                                text = "🔥 Ab nächstem Spiel: +1 Bockrunde aktiv (4 Spiele)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AmberAccent,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Row of 4 player deltas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            players.forEachIndexed { i, p ->
                                val delta = previewDeltas[i]
                                val dColor = when {
                                    delta > 0 -> ScorePositive
                                    delta < 0 -> ScoreNegative
                                    else -> ScoreZero
                                }
                                val dText = if (delta > 0) "+$delta" else "$delta"
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(Color(0x18FFFFFF), RoundedCornerShape(6.dp))
                                        .padding(vertical = 4.dp, horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(p.colorOnDark, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = p.name,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = dText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = dColor
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SUBMIT ACTION BUTTON
                Button(
                    onClick = {
                        val finalPoints = activePoints
                        val finalWinners = currentWinners
                        val finalSoloType = if (isSolo) soloType else null
                        val notes = if (isSolo) "$soloType (${if (soloistWon) "gewonnen" else "verloren"})"
                        else if (finalPoints == 0) "Nullspiel (0 Pkt.)"
                        else "Normalspiel"

                        onSubmitRound(
                            isSolo,
                            if (isSolo) soloistIndex else null,
                            finalSoloType,
                            finalWinners,
                            finalPoints,
                            applyBock,
                            triggerBockForNext,
                            notes
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_round_button")
                ) {
                    Text(
                        text = "Runde werten ($totalRoundValue Pkt.)",
                        color = Color(0xFF091F13),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickModeContent(
    isSolo: Boolean,
    players: List<Player>,
    selectedWinners: List<Int>,
    soloistIndex: Int?,
    soloistWon: Boolean,
    soloType: String,
    gamePoints: Int,
    onWinnersChange: (Int, Int) -> Unit,
    onToggleWinner: (Int) -> Unit,
    onSoloistChange: (Int) -> Unit,
    onSoloistWonChange: (Boolean) -> Unit,
    onSoloTypeChange: (String) -> Unit,
    onPointsChange: (Int) -> Unit
) {
    if (!isSolo) {
        // Normalspiel: 2 Winners
        Text(
            text = "Wer hat gewonnen? (2 Gewinner antippen)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            players.forEachIndexed { idx, player ->
                val isSelected = selectedWinners.contains(idx)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color(0x334ADE80) else TableFeltElevated,
                    border = BorderStroke(1.5.dp, if (isSelected) ScorePositive else Color(0x33FFFFFF)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onToggleWinner(idx) }
                        .testTag("winner_chip_$idx")
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(player.colorOnDark, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = player.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                            maxLines = 1
                        )
                        if (isSelected) {
                            Text(
                                text = "✓ Sieger",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ScorePositive
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Solospiel: Select 1 Soloist
        Text(
            text = "Wer hat das Solo gespielt?",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            players.forEachIndexed { idx, player ->
                val isSoloist = soloistIndex == idx
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSoloist) GoldPrimary.copy(alpha = 0.25f) else TableFeltElevated,
                    border = BorderStroke(1.5.dp, if (isSoloist) GoldPrimary else Color(0x33FFFFFF)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSoloistChange(idx) }
                        .testTag("soloist_chip_$idx")
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(player.colorOnDark, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = player.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSoloist) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSoloist) GoldLight else Color(0xFFE2E8F0),
                            maxLines = 1
                        )
                        if (isSoloist) {
                            Text(
                                text = "Solist",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Soloist outcome: Won (+3x) vs Lost (-3x)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (soloistWon) ScorePositive.copy(alpha = 0.25f) else TableFeltElevated,
                border = BorderStroke(1.dp, if (soloistWon) ScorePositive else Color(0x33FFFFFF)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSoloistWonChange(true) }
            ) {
                Text(
                    text = "Solist hat gewonnen (+3x)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (soloistWon) ScorePositive else Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (!soloistWon) ScoreNegative.copy(alpha = 0.25f) else TableFeltElevated,
                border = BorderStroke(1.dp, if (!soloistWon) ScoreNegative else Color(0x33FFFFFF)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSoloistWonChange(false) }
            ) {
                Text(
                    text = "Solist hat verloren (-3x)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!soloistWon) ScoreNegative else Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Solo Type chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Farbsolo", "Damensolo", "Bubensolo", "Fleischlos").forEach { type ->
                val isSel = soloType == type
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSel) GoldPrimary.copy(alpha = 0.2f) else Color(0x11FFFFFF),
                    border = BorderStroke(1.dp, if (isSel) GoldPrimary else Color(0x22FFFFFF)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSoloTypeChange(type) }
                ) {
                    Text(
                        text = type,
                        fontSize = 10.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) GoldLight else Color(0xFFA1A1AA),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Punktzahl wählen
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Erzielte Punktzahl:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        // Stepper for custom values
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { if (gamePoints > 0) onPointsChange(gamePoints - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Punkt abziehen", tint = GoldLight)
            }
            Text(
                text = "$gamePoints",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (gamePoints == 0) ScoreZero else AmberAccent,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
            IconButton(
                onClick = { onPointsChange(gamePoints + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Punkt hinzufügen", tint = GoldLight)
            }
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Quick points chips: 0, 1, 2, 3, 4, 5, 6, 7, 8
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(0, 1, 2, 3, 4, 5, 6, 7).forEach { pts ->
            val isSelected = gamePoints == pts
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) AmberAccent else TableFeltElevated,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPointsChange(pts) }
                    .testTag("points_chip_$pts")
            ) {
                Text(
                    text = "$pts",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF1E0E05) else Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailedModeContent(
    isSolo: Boolean,
    players: List<Player>,
    soloistIndex: Int?,
    soloistWon: Boolean,
    soloType: String,
    gamePoints: Int,
    rePlayer1: Int,
    rePlayer2: Int,
    reWon: Boolean,
    ansageRe: Boolean,
    ansageKontra: Boolean,
    keine90: Boolean,
    keine60: Boolean,
    keine30: Boolean,
    schwarz: Boolean,
    gegenDieAlten: Boolean,
    fuechseGefangen: Int,
    karlchen: Boolean,
    doppelkopfCount: Int,
    onRePlayer1Change: (Int) -> Unit,
    onRePlayer2Change: (Int) -> Unit,
    onReWonChange: (Boolean) -> Unit,
    onAnsageReChange: (Boolean) -> Unit,
    onAnsageKontraChange: (Boolean) -> Unit,
    onKeine90Change: (Boolean) -> Unit,
    onKeine60Change: (Boolean) -> Unit,
    onKeine30Change: (Boolean) -> Unit,
    onSchwarzChange: (Boolean) -> Unit,
    onGegenDieAltenChange: (Boolean) -> Unit,
    onFuechseChange: (Int) -> Unit,
    onKarlchenChange: (Boolean) -> Unit,
    onDoppelkopfChange: (Int) -> Unit,
    onPointsChange: (Int) -> Unit,
    onSoloistChange: (Int) -> Unit,
    onSoloistWonChange: (Boolean) -> Unit,
    onSoloTypeChange: (String) -> Unit
) {
    if (isSolo) {
        // In detailed mode, if solo is active, configure solo points
        Text(
            text = "Solospiel Details:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            players.forEachIndexed { idx, p ->
                FilterChip(
                    selected = soloistIndex == idx,
                    onClick = { onSoloistChange(idx) },
                    label = { Text(p.name, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (soloistWon) ScorePositive.copy(alpha = 0.25f) else TableFeltElevated,
                border = BorderStroke(1.dp, if (soloistWon) ScorePositive else Color(0x33FFFFFF)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSoloistWonChange(true) }
            ) {
                Text(
                    text = "Gewonnen (+3x)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (soloistWon) ScorePositive else Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (!soloistWon) ScoreNegative.copy(alpha = 0.25f) else TableFeltElevated,
                border = BorderStroke(1.dp, if (!soloistWon) ScoreNegative else Color(0x33FFFFFF)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSoloistWonChange(false) }
            ) {
                Text(
                    text = "Verloren (-3x)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!soloistWon) ScoreNegative else Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    } else {
        // Normalspiel Re/Kontra details
        Text(
            text = "Wer ist Re? (Kreuz-Damen Besitzer)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            players.forEachIndexed { idx, p ->
                val isRe = (idx == rePlayer1 || idx == rePlayer2)
                FilterChip(
                    selected = isRe,
                    onClick = {
                        if (!isRe) {
                            onRePlayer1Change(rePlayer2)
                            onRePlayer2Change(idx)
                        }
                    },
                    label = { Text(p.name, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Gewinner: Re vs. Kontra
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (reWon) GoldPrimary else TableFeltElevated,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onReWonChange(true) }
            ) {
                Text(
                    text = "Re gewinnt (>120)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (reWon) Color(0xFF091F13) else Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (!reWon) GoldPrimary else TableFeltElevated,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onReWonChange(false) }
            ) {
                Text(
                    text = "Kontra gewinnt (≥120)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!reWon) Color(0xFF091F13) else Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ansagen & Sonderpunkte
        Text(
            text = "Ansagen & Sonderpunkte:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OptionChip(label = "Re (+2)", checked = ansageRe, onToggle = onAnsageReChange, modifier = Modifier.weight(1f))
                OptionChip(label = "Kontra (+2)", checked = ansageKontra, onToggle = onAnsageKontraChange, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OptionChip(label = "Keine 90 (+1)", checked = keine90, onToggle = onKeine90Change, modifier = Modifier.weight(1f))
                OptionChip(label = "Keine 60 (+1)", checked = keine60, onToggle = onKeine60Change, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OptionChip(label = "Keine 30 (+1)", checked = keine30, onToggle = onKeine30Change, modifier = Modifier.weight(1f))
                OptionChip(label = "Schwarz (+1)", checked = schwarz, onToggle = onSchwarzChange, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OptionChip(label = "Gegen die Alten (+1)", checked = gegenDieAlten, onToggle = onGegenDieAltenChange, modifier = Modifier.weight(1f))
                OptionChip(label = "Karlchen (+1)", checked = karlchen, onToggle = onKarlchenChange, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Fuchs gefangen (+1 je):", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (fuechseGefangen > 0) onFuechseChange(fuechseGefangen - 1) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = GoldLight)
                    }
                    Text(text = "$fuechseGefangen", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp))
                    IconButton(onClick = { onFuechseChange(fuechseGefangen + 1) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = GoldLight)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Doppelkopf 40+ (+1 je):", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (doppelkopfCount > 0) onDoppelkopfChange(doppelkopfCount - 1) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = GoldLight)
                    }
                    Text(text = "$doppelkopfCount", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp))
                    IconButton(onClick = { onDoppelkopfChange(doppelkopfCount + 1) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = GoldLight)
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionChip(
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (checked) GoldPrimary.copy(alpha = 0.25f) else Color(0x15FFFFFF),
        border = BorderStroke(1.dp, if (checked) GoldPrimary else Color(0x22FFFFFF)),
        modifier = modifier.clickable { onToggle(!checked) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(if (checked) GoldPrimary else Color.Transparent, CircleShape)
                    .then(if (!checked) Modifier.background(Color(0x33FFFFFF), CircleShape) else Modifier),
                contentAlignment = Alignment.Center
            ) {
                if (checked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF091F13),
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal,
                color = if (checked) GoldLight else Color(0xFFCBD5E1)
            )
        }
    }
}
