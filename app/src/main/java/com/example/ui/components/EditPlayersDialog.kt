package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Player
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TableFeltCard

@Composable
fun EditPlayersDialog(
    players: List<Player>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name0 by remember { mutableStateOf(players.getOrNull(0)?.name ?: "Spieler 1") }
    var name1 by remember { mutableStateOf(players.getOrNull(1)?.name ?: "Spieler 2") }
    var name2 by remember { mutableStateOf(players.getOrNull(2)?.name ?: "Spieler 3") }
    var name3 by remember { mutableStateOf(players.getOrNull(3)?.name ?: "Spieler 4") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Spielernamen anpassen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GoldLight
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Die Reihenfolge legt auch die anfängliche Geber-Reihenfolge fest (Spieler 1 gibt in Runde 1).",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )

                PlayerNameField(index = 0, name = name0, onNameChange = { name0 = it }, player = players[0])
                PlayerNameField(index = 1, name = name1, onNameChange = { name1 = it }, player = players[1])
                PlayerNameField(index = 2, name = name2, onNameChange = { name2 = it }, player = players[2])
                PlayerNameField(index = 3, name = name3, onNameChange = { name3 = it }, player = players[3])
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name0, name1, name2, name3) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_players_button")
            ) {
                Text(
                    text = "Speichern",
                    color = Color(0xFF091F13),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = Color(0xFFA1A1AA))
            }
        },
        containerColor = TableFeltCard,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun PlayerNameField(
    index: Int,
    name: String,
    onNameChange: (String) -> Unit,
    player: Player
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Spieler ${index + 1}", color = player.colorOnDark, fontSize = 11.sp) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = player.colorOnDark,
            unfocusedBorderColor = Color(0x33FFFFFF),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("player_input_$index")
    )
}
