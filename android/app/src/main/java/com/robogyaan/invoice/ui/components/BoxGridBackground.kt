package com.robogyaan.invoice.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BoxGridBackground(
    isDarkMode: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (!enabled) {
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(color = if (isDarkMode) Color(0xFF121212) else Color(0xFFFDFBF7))
        }
        return
    }

    val backgroundColor = if (isDarkMode) Color(0xFF09090B) else Color(0xFFFDFBF7)
    val gridLineColor = if (isDarkMode) Color(0xFF222226) else Color(0xFFE5E7EB)

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(color = backgroundColor)

        val cellSize = 42.dp.toPx()
        val width = size.width
        val height = size.height

        // Horizontal lines
        var y = 0f
        while (y <= height) {
            drawLine(
                color = gridLineColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += cellSize
        }

        // Vertical lines
        var x = 0f
        while (x <= width) {
            drawLine(
                color = gridLineColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += cellSize
        }
    }
}
