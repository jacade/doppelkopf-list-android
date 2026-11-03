package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameRound
import com.example.data.model.Player
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ScoreNegative
import com.example.ui.theme.ScorePositive
import com.example.ui.theme.ScoreZero
import com.example.ui.theme.TableFeltCard
import com.example.ui.theme.TableFeltElevated
import com.example.viewmodel.PlayerStats

@Composable
fun PlayerChartAndStatsView(
    players: List<Player>,
    rounds: List<GameRound>,
    stats: List<PlayerStats>,
    onSharePdf: () -> Unit,
    onShareCsv: () -> Unit,
    onShareText: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // SECTION: SCORE PROGRESSION GRAPH
        Text(
            text = "Punkte-Verlauf",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GoldLight
        )
        Text(
            text = "Entwicklung des Spielstands über alle Runden",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFA1A1AA)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = TableFeltCard),
            border = BorderStroke(1.dp, Color(0x33D4AF37)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("score_chart_card")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Legend Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    players.forEach { p ->
                        val current = stats.firstOrNull { it.player.id == p.id }?.currentScore ?: 0
                        val scoreStr = if (current > 0) "+$current" else "$current"
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(p.colorOnDark, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${p.name} ($scoreStr)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (rounds.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Der Punkteverlauf wird nach der ersten gewerteten Runde sichtbar.",
                            fontSize = 12.sp,
                            color = Color(0xFFA1A1AA),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    ScoreProgressionCanvas(
                        players = players,
                        rounds = rounds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION: PLAYER STATS TABLE
        Text(
            text = "Spieler-Statistiken",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GoldLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = TableFeltCard),
            border = BorderStroke(1.dp, Color(0x33D4AF37)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TableFeltElevated)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Rang & Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight, modifier = Modifier.weight(1.8f))
                    Text(text = "Punkte", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    Text(text = "S/N (Quote)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight, textAlign = TextAlign.Center, modifier = Modifier.weight(1.4f))
                    Text(text = "Soli", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                }
                HorizontalDivider(color = Color(0x33D4AF37), thickness = 1.dp)

                stats.sortedBy { it.rank }.forEach { s ->
                    val scoreColor = when {
                        s.currentScore > 0 -> ScorePositive
                        s.currentScore < 0 -> ScoreNegative
                        else -> ScoreZero
                    }
                    val winRate = if (s.totalGamesPlayed > 0) (s.gamesWon * 100 / s.totalGamesPlayed) else 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1.8f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${s.rank}.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                modifier = Modifier.width(18.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(s.player.colorOnDark, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = s.player.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        val sign = if (s.currentScore > 0) "+" else ""
                        Text(
                            text = "$sign${s.currentScore}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = scoreColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${s.gamesWon} / ${s.gamesLost} ($winRate%)",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.4f)
                        )

                        Text(
                            text = "${s.soliWon}/${s.soliPlayed}",
                            fontSize = 11.sp,
                            color = if (s.soliPlayed > 0) AmberAccent else Color(0xFFA1A1AA),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    HorizontalDivider(color = Color(0x15FFFFFF), thickness = 0.8.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION: EXPORTS (PDF, CSV, TEXT)
        Text(
            text = "Export-Funktionen",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GoldLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        // PDF Export Primary Button
        Button(
            onClick = onSharePdf,
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("share_pdf_button")
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = Color(0xFF091F13),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Als PDF exportieren (Tabelle & Stand)",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF091F13)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CSV & Text buttons in Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onShareCsv,
                colors = ButtonDefaults.buttonColors(containerColor = TableFeltElevated),
                border = BorderStroke(1.dp, GoldPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("share_csv_button")
            ) {
                Icon(
                    imageVector = Icons.Default.TableChart,
                    contentDescription = null,
                    tint = GoldLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "CSV Export",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            }

            Button(
                onClick = onShareText,
                colors = ButtonDefaults.buttonColors(containerColor = TableFeltElevated),
                border = BorderStroke(1.dp, Color(0x33D4AF37)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("share_text_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = GoldLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Text teilen",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ScoreProgressionCanvas(
    players: List<Player>,
    rounds: List<GameRound>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val totalRounds = rounds.size
        if (totalRounds == 0) return@Canvas

        val scoresPerPlayer = List(4) { playerIdx ->
            mutableListOf(0).apply {
                rounds.forEach { r ->
                    add(r.playerRunningScores.getOrElse(playerIdx) { 0 })
                }
            }
        }

        var maxScore = 5
        var minScore = -5
        scoresPerPlayer.forEach { list ->
            list.forEach { score ->
                if (score > maxScore) maxScore = score
                if (score < minScore) minScore = score
            }
        }
        val range = (maxScore - minScore).coerceAtLeast(10).toFloat()

        val paddingLeft = 32f
        val paddingRight = 24f
        val paddingTop = 16f
        val paddingBottom = 24f

        val plotWidth = size.width - paddingLeft - paddingRight
        val plotHeight = size.height - paddingTop - paddingBottom

        fun mapX(roundIndex: Int): Float {
            return paddingLeft + (roundIndex.toFloat() / totalRounds.toFloat()) * plotWidth
        }

        fun mapY(score: Int): Float {
            val normalized = (score - minScore).toFloat() / range
            return paddingTop + (1f - normalized) * plotHeight
        }

        val zeroY = mapY(0)
        drawLine(
            color = Color(0x44FFFFFF),
            start = Offset(paddingLeft, zeroY),
            end = Offset(size.width - paddingRight, zeroY),
            strokeWidth = 1.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        players.forEachIndexed { playerIdx, player ->
            val points = scoresPerPlayer[playerIdx]
            val path = Path()

            points.forEachIndexed { roundIdx, score ->
                val x = mapX(roundIdx)
                val y = mapY(score)
                if (roundIdx == 0) path.moveTo(x, y)
                else path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = player.colorOnDark,
                style = Stroke(width = 3f)
            )

            points.forEachIndexed { roundIdx, score ->
                val x = mapX(roundIdx)
                val y = mapY(score)
                drawCircle(
                    color = player.colorOnDark,
                    radius = 4f,
                    center = Offset(x, y)
                )
            }
        }
    }
}
