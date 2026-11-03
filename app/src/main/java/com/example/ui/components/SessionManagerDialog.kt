package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.GameSessionEntity
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ScoreNegative
import com.example.ui.theme.TableFeltCard
import com.example.ui.theme.TableFeltElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SessionManagerDialog(
    allSessions: List<GameSessionEntity>,
    currentSessionId: Long,
    onSelectSession: (Long) -> Unit,
    onCreateNewSession: (String, String, String, String, String) -> Unit,
    onEditCurrentSession: (String, String, String, String, String) -> Unit,
    onDeleteCurrentSession: () -> Unit,
    onDismiss: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(16.dp))
                .testTag("session_manager_dialog"),
            color = TableFeltCard,
            border = BorderStroke(1.5.dp, GoldPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Spielrunden verwalten",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen", tint = Color(0xFFA1A1AA))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List of Sessions
                Text(
                    text = "Gespeicherte Runden & Listen:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(allSessions, key = { it.sessionId }) { session ->
                        val isSelected = session.sessionId == currentSessionId
                        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
                        val dateStr = dateFormat.format(Date(session.updatedAt))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) TableFeltElevated else Color(0x15FFFFFF),
                            border = BorderStroke(1.dp, if (isSelected) GoldPrimary else Color(0x22FFFFFF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectSession(session.sessionId)
                                    onDismiss()
                                }
                                .testTag("session_item_${session.sessionId}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = session.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) GoldLight else Color.White
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GoldPrimary
                                            ) {
                                                Text(
                                                    text = "Aktiv",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF091F13),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${session.player0Name}, ${session.player1Name}, ${session.player2Name}, ${session.player3Name} • $dateStr",
                                        fontSize = 11.sp,
                                        color = Color(0xFFA1A1AA)
                                    )
                                }

                                if (isSelected) {
                                    Row {
                                        IconButton(
                                            onClick = { showEditDialog = true },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Bearbeiten", tint = GoldLight, modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(
                                            onClick = { showDeleteConfirm = true },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = ScoreNegative, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Create New List Button
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("create_new_session_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF091F13), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Neue Liste anlegen",
                        color = Color(0xFF091F13),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        val current = allSessions.firstOrNull { it.sessionId == currentSessionId }
        CreateOrEditSessionDialog(
            isEdit = false,
            initialTitle = "Runde ${allSessions.size + 1}",
            initialP0 = current?.player0Name ?: "Spieler 1",
            initialP1 = current?.player1Name ?: "Spieler 2",
            initialP2 = current?.player2Name ?: "Spieler 3",
            initialP3 = current?.player3Name ?: "Spieler 4",
            onDismiss = { showCreateDialog = false },
            onSave = { t, p0, p1, p2, p3 ->
                onCreateNewSession(t, p0, p1, p2, p3)
                showCreateDialog = false
                onDismiss()
            }
        )
    }

    if (showEditDialog) {
        val current = allSessions.firstOrNull { it.sessionId == currentSessionId }
        if (current != null) {
            CreateOrEditSessionDialog(
                isEdit = true,
                initialTitle = current.title,
                initialP0 = current.player0Name,
                initialP1 = current.player1Name,
                initialP2 = current.player2Name,
                initialP3 = current.player3Name,
                onDismiss = { showEditDialog = false },
                onSave = { t, p0, p1, p2, p3 ->
                    onEditCurrentSession(t, p0, p1, p2, p3)
                    showEditDialog = false
                }
            )
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = {
                Text(text = "Liste löschen?", fontWeight = FontWeight.Bold, color = GoldLight)
            },
            text = {
                Text(
                    text = "Möchtest du diese Liste und alle darin enthaltenen Runden unwiderruflich löschen?",
                    color = Color(0xFFCBD5E1)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCurrentSession()
                        showDeleteConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Löschen", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Abbrechen", color = Color(0xFFA1A1AA))
                }
            },
            containerColor = TableFeltCard
        )
    }
}

@Composable
private fun CreateOrEditSessionDialog(
    isEdit: Boolean,
    initialTitle: String,
    initialP0: String,
    initialP1: String,
    initialP2: String,
    initialP3: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var p0 by remember { mutableStateOf(initialP0) }
    var p1 by remember { mutableStateOf(initialP1) }
    var p2 by remember { mutableStateOf(initialP2) }
    var p3 by remember { mutableStateOf(initialP3) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "Liste bearbeiten" else "Neue Runde / Liste anlegen",
                fontWeight = FontWeight.Bold,
                color = GoldLight
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Name der Liste (z.B. Stammtisch)", color = GoldLight, fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Spieler (Reihenfolge bestimmt Geber):",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = p0,
                        onValueChange = { p0 = it },
                        label = { Text("Sp 1", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = p1,
                        onValueChange = { p1 = it },
                        label = { Text("Sp 2", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = p2,
                        onValueChange = { p2 = it },
                        label = { Text("Sp 3", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = p3,
                        onValueChange = { p3 = it },
                        label = { Text("Sp 4", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, p0, p1, p2, p3) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text(
                    text = if (isEdit) "Speichern" else "Liste erstellen",
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
