package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.GameRound
import com.example.data.model.Player
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PaperDivider
import com.example.ui.theme.PaperRowAlt
import com.example.ui.theme.PaperSheetBg
import com.example.ui.theme.PaperTextPrimary
import com.example.ui.theme.PaperTextSecondary
import com.example.ui.theme.ScoreLossPaper
import com.example.ui.theme.ScoreLossPaperBg
import com.example.ui.theme.ScoreWinPaper
import com.example.ui.theme.ScoreWinPaperBg
import com.example.ui.theme.ScoreZeroPaper
import com.example.ui.theme.ScoreZeroPaperBg
import com.example.ui.theme.TableFeltCard
import com.example.ui.theme.TableFeltElevated

@Composable
fun RoundHistoryTable(
    players: List<Player>,
    rounds: List<GameRound>,
    nextDealerPlayer: Player,
    activeBockRoundsCount: Int,
    currentBockMultiplier: Int,
    bockGamesRemaining: Int,
    onUndoClick: () -> Unit,
    onAddNewRoundClick: () -> Unit,
    onAddBockForNext: () -> Unit = {},
    onUpdateBockDirectly: (Int, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var showUndoConfirmDialog by remember { mutableStateOf(false) }
    var showBockManageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        // Subtle Top Bar: Next Dealer & (if active) Bockrunde info + Undo button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Next Dealer & Active Bock Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Next dealer chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TableFeltElevated,
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🂠 Geber:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(nextDealerPlayer.colorOnDark, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = nextDealerPlayer.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // Bock Status badge (if active) or subtle "+ Bock" chip
                if (activeBockRoundsCount > 0 && bockGamesRemaining > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberAccent.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, AmberAccent),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showBockManageDialog = true }
                            .testTag("active_bock_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Bock x$currentBockMultiplier ($bockGamesRemaining)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberAccent
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x18FFFFFF),
                        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onAddBockForNext() }
                            .testTag("quick_add_bock_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "+ Bock",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldLight
                            )
                        }
                    }
                }
            }

            // Undo button
            if (rounds.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x22FFFFFF),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showUndoConfirmDialog = true }
                        .testTag("undo_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Letzte Runde rückgängig machen",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Zurück",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (rounds.isEmpty()) {
            EmptyRoundsPlaceholder(onAddNewRoundClick = onAddNewRoundClick)
        } else {
            // Maximized Table Card Container (Crisp White Paper Scorepad Look)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PaperSheetBg),
                border = BorderStroke(1.5.dp, GoldPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("rounds_table")
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Table Column Header Row
                    TableHeaderRow(players = players)
                    HorizontalDivider(color = Color(0x33D4AF37), thickness = 1.2.dp)

                    // ANTICHRONOLOGICAL LIST: latest round #N is on TOP!
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        itemsIndexed(rounds.reversed(), key = { _, it -> it.id }) { index, round ->
                            SingleLineTableRow(round = round, players = players, isEven = index % 2 == 0)
                            HorizontalDivider(color = PaperDivider, thickness = 0.8.dp)
                        }
                    }
                }
            }
        }
    }

    if (showUndoConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showUndoConfirmDialog = false },
            title = {
                Text(
                    text = "Letztes Spiel rückgängig?",
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            },
            text = {
                val lastRound = rounds.lastOrNull()
                Text(
                    text = "Möchtest du Spiel #${lastRound?.roundNumber ?: ""} wirklich aus der Wertung entfernen? Die Punkte werden auf den vorherigen Stand zurückgesetzt.",
                    color = Color(0xFFCBD5E1)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUndoClick()
                        showUndoConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Löschen", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUndoConfirmDialog = false }) {
                    Text("Abbrechen", color = Color(0xFFA1A1AA))
                }
            },
            containerColor = TableFeltCard
        )
    }

    if (showBockManageDialog) {
        AlertDialog(
            onDismissRequest = { showBockManageDialog = false },
            title = {
                Text(
                    text = "Bockrunden Status",
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Aktuell: Stufe x$currentBockMultiplier ($activeBockRoundsCount gleichzeitig aktiv)\nNoch $bockGamesRemaining Spiele werden automatisch verdoppelt.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Bockrunden verdoppeln sich kumulativ (1=x2, 2=x4, 3=x8, 4=x16) und gelten für je 4 Spiele ab dem nächsten Spiel.",
                        color = Color(0xFFA1A1AA),
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddBockForNext()
                        showBockManageDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberAccent)
                ) {
                    Text("+4 Spiele (Bock)", color = Color(0xFF1E0E05), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        onUpdateBockDirectly(0, 0)
                        showBockManageDialog = false
                    }) {
                        Text("Beenden", color = Color(0xFFF87171))
                    }
                    TextButton(onClick = { showBockManageDialog = false }) {
                        Text("Schließen", color = Color(0xFFA1A1AA))
                    }
                }
            },
            containerColor = TableFeltCard
        )
    }
}

@Composable
private fun TableHeaderRow(players: List<Player>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F5F9))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Spielnummer Column
        Text(
            text = "#",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF334155),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(32.dp)
        )

        // Geber Column
        Text(
            text = "Geber",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334155),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(44.dp)
        )

        // Spielwert Column
        Text(
            text = "Spiel",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB45309),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(52.dp)
        )

        // 4 Player Columns
        players.forEach { player ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 4.dp)
                        .background(player.colorOnLight, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = player.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaperTextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SingleLineTableRow(
    round: GameRound,
    players: List<Player>,
    isEven: Boolean = true
) {
    val dealerPlayer = players.getOrNull(round.dealerIndex)
    val dealerName = dealerPlayer?.initials ?: "Sp ${round.dealerIndex + 1}"
    val rowBackground = if (isEven) PaperSheetBg else PaperRowAlt

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBackground)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Spielnummer
        Text(
            text = "${round.roundNumber}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PaperTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(32.dp)
        )

        // Geber
        Box(
            modifier = Modifier.width(44.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFF1F5F9),
                border = BorderStroke(0.8.dp, Color(0xFFCBD5E1))
            ) {
                Text(
                    text = dealerName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaperTextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        // Spielwert
        Row(
            modifier = Modifier.width(52.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${round.totalRoundValue}",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (round.isZeroPointsGame) ScoreZeroPaper else PaperTextPrimary,
                textAlign = TextAlign.Center
            )
            if (round.bockMultiplier > 1) {
                Text(
                    text = "x${round.bockMultiplier}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFB45309), // Warm amber/gold
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }

        // 4 Players: ONLY ONE ROW, Green if Won, Red if Lost, Neutral if Zero on White Paper!
        round.playerRunningScores.forEachIndexed { idx, runningScore ->
            val delta = round.playerScoreDeltas.getOrElse(idx) { 0 }
            val (scoreColor, scoreBg) = when {
                delta > 0 -> Pair(ScoreWinPaper, ScoreWinPaperBg)   // Soft green badge + Deep Emerald
                delta < 0 -> Pair(ScoreLossPaper, ScoreLossPaperBg) // Soft red badge + Crisp Crimson
                else -> Pair(ScoreZeroPaper, ScoreZeroPaperBg)      // Soft neutral badge + Slate
            }

            val formattedScore = when {
                runningScore > 0 -> "+$runningScore"
                else -> "$runningScore"
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = scoreBg,
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = formattedScore,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyRoundsPlaceholder(onAddNewRoundClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PaperSheetBg),
        border = BorderStroke(1.5.dp, GoldPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(0xFFFEF3C7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = null,
                    tint = Color(0xFFB45309),
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Noch keine Runden in dieser Liste",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PaperTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Die Tabelle zeigt den Punktestand antichronologisch (neueste Runde oben). Trage das erste Spiel ein!",
                style = MaterialTheme.typography.bodySmall,
                color = PaperTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddNewRoundClick,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF091F13), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Erstes Spiel eintragen",
                    color = Color(0xFF091F13),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
