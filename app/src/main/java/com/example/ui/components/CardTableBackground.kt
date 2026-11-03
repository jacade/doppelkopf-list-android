package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.TableFelt
import com.example.ui.theme.TableFeltDark

@Composable
fun CardTableBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        TableFelt,
                        TableFeltDark,
                        Color(0xFF06140C)
                    ),
                    radius = 1800f
                )
            )
    ) {
        // Subtle decorative felt texture lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 48f
            var x = 0f
            while (x < size.width + size.height) {
                drawLine(
                    color = Color(0x06FFFFFF),
                    start = Offset(x, 0f),
                    end = Offset(0f, x),
                    strokeWidth = 1f
                )
                x += step
            }
        }
        content()
    }
}
