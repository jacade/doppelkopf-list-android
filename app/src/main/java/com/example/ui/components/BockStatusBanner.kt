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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TableFeltCard
import com.example.ui.theme.TableFeltElevated

@Composable
fun BockStatusBanner(
    activeBockRoundsCount: Int, // 0..4
    bockGamesRemaining: Int,
    currentMultiplier: Int, // 1, 2, 4, 8, 16
    onAddBockRound: () -> Unit,
    onSetBockDirectly: (Int, Int) -> Unit,
    onResetBock: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAdjustDialog by remember { mutableStateOf(false) }
    val isBockActive = activeBockRoundsCount > 0 && bockGamesRemaining > 0

    val backgroundBrush = if (isBockActive) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF2C1608),
                Color(0xFF1E2818)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                TableFeltCard,
                TableFeltElevated
            )
        )
    }

    val borderColor = if (isBockActive) AmberAccent else Color(0x33D4AF37)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .background(backgroundBrush, RoundedCornerShape(12.dp))
            .testTag("bock_status_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon + Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { showAdjustDialog = true }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isBockActive) AmberAccent.copy(alpha = 0.2f) else Color(0x1AFFFFFF),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Bockrunden Status",
                        tint = if (isBockActive) AmberAccent else Color(0xFFA1A1AA),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isBockActive) "Bockrunde aktiv!" else "Keine Bockrunde",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isBockActive) AmberAccent else Color.White
                        )
                        if (isBockActive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AmberAccent
                            ) {
                                Text(
                                    text = "x$currentMultiplier",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E0E05),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = if (isBockActive) {
                            "$activeBockRoundsCount parallel • noch $bockGamesRemaining Spiele"
                        } else {
                            "Tippen zum Anpassen oder Schnellstart"
                        },
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }

            // Right: Quick Add Button (+1 Bockrunde)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isBockActive) AmberAccent else GoldPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAddBockRound() }
                    .testTag("add_bock_quick_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF091F13),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "+1 Bock",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF091F13)
                    )
                }
            }
        }
    }

    if (showAdjustDialog) {
        AdjustBockDialog(
            currentCount = activeBockRoundsCount,
            currentGames = bockGamesRemaining,
            onDismiss = { showAdjustDialog = false },
            onApply = { count, games ->
                onSetBockDirectly(count, games)
                showAdjustDialog = false
            },
            onReset = {
                onResetBock()
                showAdjustDialog = false
            }
        )
    }
}

@Composable
private fun AdjustBockDialog(
    currentCount: Int,
    currentGames: Int,
    onDismiss: () -> Unit,
    onApply: (Int, Int) -> Unit,
    onReset: () -> Unit
) {
    var count by remember { mutableIntStateOf(currentCount) }
    var games by remember { mutableIntStateOf(currentGames) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Bockrunden verwalten",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GoldLight
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Bockrunden verdoppeln sich exponentiell bei parallelen Runden:\n1 Runde = x2, 2 Runden = x4, 3 Runden = x8, 4 Runden = x16.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1)
                )

                // Level Selection (0, 1, 2, 3, 4)
                Text(
                    text = "Aktive Bockrunden (Stufe):",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        0 to "Aus (x1)",
                        1 to "1 (x2)",
                        2 to "2 (x4)",
                        3 to "3 (x8)",
                        4 to "4 (x16)"
                    ).forEach { (tier, label) ->
                        val selected = count == tier
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) AmberAccent else TableFeltCard,
                            border = BorderStroke(1.dp, if (selected) AmberAccent else Color(0x33D4AF37)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    count = tier
                                    games = if (tier == 0) 0 else if (games <= 0) tier * 4 else games
                                }
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) Color(0xFF1E0E05) else Color.White,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Remaining games adjust
                if (count > 0) {
                    Text(
                        text = "Verbleibende Bock-Spiele: $games",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (games > 1) games-- },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("-1 Spiel")
                        }
                        Button(
                            onClick = { games += 4 },
                            colors = ButtonDefaults.buttonColors(containerColor = TableFeltElevated),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+4 Spiele")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(count, games) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Speichern", color = Color(0xFF091F13), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onReset) {
                    Text("Zurücksetzen", color = Color(0xFFF87171))
                }
                TextButton(onClick = onDismiss) {
                    Text("Abbrechen", color = Color(0xFFA1A1AA))
                }
            }
        },
        containerColor = TableFeltCard
    )
}
