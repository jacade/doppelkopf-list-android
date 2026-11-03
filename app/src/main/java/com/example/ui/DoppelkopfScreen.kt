package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddRoundDialog
import com.example.ui.components.CardTableBackground
import com.example.ui.components.PlayerChartAndStatsView
import com.example.ui.components.RoundHistoryTable
import com.example.ui.components.SessionManagerDialog
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TableFeltCard
import com.example.ui.theme.TableFeltElevated
import com.example.viewmodel.DoppelkopfViewModel

@Composable
fun DoppelkopfScreen(
    viewModel: DoppelkopfViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var currentScreenTab by remember { mutableIntStateOf(0) } // 0: Kartentisch, 1: Auswertung & Charts
    var showAddRoundDialog by remember { mutableStateOf(false) }
    var showSessionManagerDialog by remember { mutableStateOf(false) }

    CardTableBackground {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = Color.Transparent,
            topBar = {
                // Sleek Top Bar with List Selector extending behind status bar
                Surface(
                    color = TableFeltCard.copy(alpha = 0.98f),
                    border = BorderStroke(0.5.dp, Color(0x22D4AF37)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: List Selector Dropdown Button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TableFeltElevated,
                            border = BorderStroke(1.dp, Color(0x33D4AF37)),
                            modifier = Modifier
                                .clickable { showSessionManagerDialog = true }
                                .testTag("list_selector_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatListBulleted,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = uiState.currentSession.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Listen wechseln",
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Right: Quick Add List / Switch Icon
                        IconButton(
                            onClick = { showSessionManagerDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("open_sessions_icon")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Neue Liste",
                                tint = GoldPrimary
                            )
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = TableFeltCard,
                    contentColor = GoldLight,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreenTab == 0,
                        onClick = { currentScreenTab = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = "Tabelle"
                            )
                        },
                        label = {
                            Text(
                                text = "Tabelle",
                                fontWeight = if (currentScreenTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF091F13),
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = Color(0xFFA1A1AA),
                            unselectedTextColor = Color(0xFFA1A1AA)
                        ),
                        modifier = Modifier.testTag("nav_tab_table")
                    )

                    NavigationBarItem(
                        selected = currentScreenTab == 1,
                        onClick = { currentScreenTab = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "Auswertung & Export"
                            )
                        },
                        label = {
                            Text(
                                text = "Auswertung",
                                fontWeight = if (currentScreenTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF091F13),
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = Color(0xFFA1A1AA),
                            unselectedTextColor = Color(0xFFA1A1AA)
                        ),
                        modifier = Modifier.testTag("nav_tab_stats")
                    )
                }
            },
            floatingActionButton = {
                if (currentScreenTab == 0) {
                    ExtendedFloatingActionButton(
                        onClick = { showAddRoundDialog = true },
                        containerColor = GoldPrimary,
                        contentColor = Color(0xFF091F13),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        text = {
                            Text(
                                text = "Runde eintragen",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .testTag("fab_add_round")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (currentScreenTab == 0) {
                    // MAIN TABLE IN FOCUS: MAXIMIZED HEIGHT, NO CLUTTER
                    val nextDealer = uiState.players.getOrElse(uiState.nextDealerIndex) { uiState.players[0] }

                    RoundHistoryTable(
                        players = uiState.players,
                        rounds = uiState.rounds,
                        nextDealerPlayer = nextDealer,
                        activeBockRoundsCount = uiState.currentSession.activeBockRoundsCount,
                        currentBockMultiplier = uiState.currentBockMultiplier,
                        bockGamesRemaining = uiState.currentSession.bockGamesRemaining,
                        onUndoClick = { viewModel.undoLastRound() },
                        onAddNewRoundClick = { showAddRoundDialog = true },
                        onAddBockForNext = { viewModel.addBockRoundsForNextGames(1) },
                        onUpdateBockDirectly = { count, games -> viewModel.updateBockDirectly(count, games) },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // STATS & VISUALIZATION TAB
                    PlayerChartAndStatsView(
                        players = uiState.players,
                        rounds = uiState.rounds,
                        stats = uiState.stats,
                        onSharePdf = { viewModel.sharePdf(context) },
                        onShareCsv = { viewModel.shareExport(context, asCsv = true) },
                        onShareText = { viewModel.shareExport(context, asCsv = false) }
                    )
                }
            }
        }
    }

    // DIALOGS
    if (showAddRoundDialog) {
        val nextRoundNum = uiState.rounds.size + 1
        val dealer = uiState.players.getOrElse(uiState.nextDealerIndex) { uiState.players[0] }

        AddRoundDialog(
            roundNumber = nextRoundNum,
            dealerPlayer = dealer,
            players = uiState.players,
            activeBockRoundsCount = uiState.currentSession.activeBockRoundsCount,
            currentBockMultiplier = uiState.currentBockMultiplier,
            bockGamesRemaining = uiState.currentSession.bockGamesRemaining,
            onDismiss = { showAddRoundDialog = false },
            onSubmitRound = { isSolo, soloistIndex, soloType, winnerIndices, gamePoints, applyBock, triggerBockForNext, notes ->
                viewModel.submitRound(
                    isSolo = isSolo,
                    soloistIndex = soloistIndex,
                    soloType = soloType,
                    winnerIndices = winnerIndices,
                    gamePoints = gamePoints,
                    applyBock = applyBock,
                    triggerBockForNext = triggerBockForNext,
                    customNotes = notes
                )
                showAddRoundDialog = false
            }
        )
    }

    if (showSessionManagerDialog) {
        SessionManagerDialog(
            allSessions = uiState.allSessions,
            currentSessionId = uiState.currentSession.sessionId,
            onSelectSession = { id -> viewModel.selectSession(id) },
            onCreateNewSession = { title, p0, p1, p2, p3 ->
                viewModel.createNewSession(title, p0, p1, p2, p3)
            },
            onEditCurrentSession = { title, p0, p1, p2, p3 ->
                viewModel.updateSessionDetails(title, p0, p1, p2, p3)
            },
            onDeleteCurrentSession = {
                viewModel.deleteCurrentSession()
            },
            onDismiss = { showSessionManagerDialog = false }
        )
    }
}
