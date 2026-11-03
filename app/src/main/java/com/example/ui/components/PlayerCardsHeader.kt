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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.Player
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ScoreNegative
import com.example.ui.theme.ScorePositive
import com.example.ui.theme.ScoreZero
import com.example.ui.theme.TableFeltCard
import com.example.ui.theme.TableFeltElevated
import com.example.viewmodel.PlayerStats

@Composable
fun PlayerCardsHeader(
    players: List<Player>,
    stats: List<PlayerStats>,
    nextDealerIndex: Int,
    onEditPlayersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Spieltisch",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
                Text(
                    text = " • 4 Spieler",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TableFeltElevated,
                border = BorderStroke(1.dp, Color(0x33D4AF37)),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onEditPlayersClick() }
                    .testTag("edit_players_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Spielernamen anpassen",
                        tint = GoldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "Namen ändern",
                        fontSize = 11.sp,
                        color = GoldLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 4 Player Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            players.forEachIndexed { index, player ->
                val playerStat = stats.firstOrNull { it.player.id == player.id }
                val score = playerStat?.currentScore ?: 0
                val rank = playerStat?.rank ?: (index + 1)
                val isNextDealer = (index == nextDealerIndex)

                SinglePlayerCard(
                    player = player,
                    score = score,
                    rank = rank,
                    isNextDealer = isNextDealer,
                    onClick = onEditPlayersClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("player_card_$index")
                )
            }
        }
    }
}

@Composable
private fun SinglePlayerCard(
    player: Player,
    score: Int,
    rank: Int,
    isNextDealer: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isNextDealer) GoldPrimary else Color(0x22FFFFFF)
    val borderWidth = if (isNextDealer) 1.5.dp else 1.dp
    val scoreColor = when {
        score > 0 -> ScorePositive
        score < 0 -> ScoreNegative
        else -> ScoreZero
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isNextDealer) TableFeltElevated else TableFeltCard
        ),
        border = BorderStroke(borderWidth, borderColor),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top tag: Dealer or Rank
            if (isNextDealer) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = GoldPrimary,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Text(
                        text = "🂠 GEBER",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF091F13),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            } else {
                Text(
                    text = "${rank}. Platz",
                    fontSize = 9.sp,
                    color = Color(0xFFA1A1AA),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // Player Avatar Dot with initial
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(22.dp)
                    .background(player.colorOnDark.copy(alpha = 0.25f), CircleShape)
            ) {
                Text(
                    text = player.initials,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = player.colorOnDark
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Player Name
            Text(
                text = player.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Current Score
            val scoreText = if (score > 0) "+$score" else "$score"
            Text(
                text = scoreText,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = scoreColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
