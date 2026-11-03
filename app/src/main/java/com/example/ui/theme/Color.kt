package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Doppelkopf Card Table Palette - Deep Felt Green & Gold
val TableFeltDark = Color(0xFF091F13)
val TableFelt = Color(0xFF0E2A1A)
val TableFeltCard = Color(0xFF143B25)
val TableFeltCardBorder = Color(0x33D4AF37)
val TableFeltElevated = Color(0xFF1C4C30)

// Score Sheet Paper Palette (High Contrast on Table)
val PaperSheetBg = Color(0xFFFFFFFF)
val PaperRowAlt = Color(0xFFF8FAFC)
val PaperDivider = Color(0xFFE2E8F0)
val PaperTextPrimary = Color(0xFF0F172A)
val PaperTextSecondary = Color(0xFF475569)
val ScoreWinPaper = Color(0xFF047857)   // Deep Emerald Green (High Contrast AA/AAA)
val ScoreLossPaper = Color(0xFFDC2626)  // Crisp Crimson Red (High Contrast AA/AAA)
val ScoreZeroPaper = Color(0xFF64748B)  // Neutral Slate
val ScoreWinPaperBg = Color(0xFFDCFCE7) // Soft Green Pill Background
val ScoreLossPaperBg = Color(0xFFFEE2E2)// Soft Red Pill Background
val ScoreZeroPaperBg = Color(0xFFF1F5F9)// Soft Neutral Pill Background

// Gold & Brass Accents
val GoldPrimary = Color(0xFFD4AF37)
val GoldLight = Color(0xFFF3E5AB)
val GoldDark = Color(0xFFAA820A)
val AmberAccent = Color(0xFFF59E0B)

// Score Deltas (for dark surfaces)
val ScorePositive = Color(0xFF4ADE80)
val ScoreNegative = Color(0xFFF87171)
val ScoreZero = Color(0xFF94A3B8)

// Playing Card Colors
val CardIvory = Color(0xFFFAF7EE)
val CardIvoryDark = Color(0xFFEDE8D8)
val SuitBlack = Color(0xFF18181B)
val SuitRed = Color(0xFFDC2626)

// Neutral Text
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFFCBD5E1)
val TextMuted = Color(0xFF94A3B8)

// Player Distinct Colors (Optimized for High Contrast and Night/Red-Light Filters)
// Light paper backgrounds: Saturated, distinct hues (WCAG AA/AAA)
val PlayerColor0 = Color(0xFF0284C7) // Sky Cyan (retains high luminance under red-light filters)
val PlayerColor1 = Color(0xFFD97706) // Rich Amber
val PlayerColor2 = Color(0xFF7C3AED) // Royal Purple (distinguishes from green felt & doesn't clash with red/amber)
val PlayerColor3 = Color(0xFFE11D48) // Vivid Crimson Rose

// Dark felt table backgrounds: High-luminance pastel/bright variants (won't look black or blend with felt)
val PlayerColor0Dark = Color(0xFF38BDF8) // Bright Cyan/Sky (remains luminous and visible with red-light filter)
val PlayerColor1Dark = Color(0xFFFBBF24) // Bright Golden Amber
val PlayerColor2Dark = Color(0xFFC084FC) // Bright Lilac / Lavender (pops sharply against dark green felt)
val PlayerColor3Dark = Color(0xFFFB7185) // Bright Coral / Rose
