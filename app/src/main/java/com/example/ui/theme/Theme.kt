package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DoppelkopfColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = TableFeltDark,
    primaryContainer = TableFeltElevated,
    onPrimaryContainer = GoldLight,
    secondary = AmberAccent,
    onSecondary = TableFeltDark,
    secondaryContainer = TableFeltCard,
    onSecondaryContainer = GoldLight,
    background = TableFeltDark,
    onBackground = TextPrimary,
    surface = TableFelt,
    onSurface = TextPrimary,
    surfaceVariant = TableFeltCard,
    onSurfaceVariant = TextSecondary,
    outline = TableFeltCardBorder,
    error = ScoreNegative,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DoppelkopfColorScheme,
        typography = Typography,
        content = content
    )
}
