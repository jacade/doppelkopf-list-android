package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.PlayerColor0
import com.example.ui.theme.PlayerColor0Dark
import com.example.ui.theme.PlayerColor1
import com.example.ui.theme.PlayerColor1Dark
import com.example.ui.theme.PlayerColor2
import com.example.ui.theme.PlayerColor2Dark
import com.example.ui.theme.PlayerColor3
import com.example.ui.theme.PlayerColor3Dark

data class Player(
    val id: Int, // 0 to 3
    val name: String
) {
    val color: Color
        get() = colorOnLight

    val colorOnLight: Color
        get() = when (id % 4) {
            0 -> PlayerColor0
            1 -> PlayerColor1
            2 -> PlayerColor2
            else -> PlayerColor3
        }

    val colorOnDark: Color
        get() = when (id % 4) {
            0 -> PlayerColor0Dark
            1 -> PlayerColor1Dark
            2 -> PlayerColor2Dark
            else -> PlayerColor3Dark
        }

    val initials: String
        get() = name.trim().split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()
            .ifEmpty { (id + 1).toString() }
}
