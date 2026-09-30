package com.robogyaan.invoice.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.*

@Composable
fun BoxGridBackground(
    isDarkMode: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (!enabled) {
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(color = if (isDarkMode) Color(0xFF09090B) else Color(0xFFFDFBF7))
        }
        return
    }

    val backgroundColor = if (isDarkMode) Color(0xFF09090B) else Color(0xFFFDFBF7)
    val gridLineColor = if (isDarkMode) Color(0x33FFFFFF) else Color(0x28000000)
    val swellAuraColor = if (isDarkMode) Color(0x2BFFE600) else Color(0x40FFE600)

    val coroutineScope = rememberCoroutineScope()
    var touchPos by remember { mutableStateOf<Offset?>(null) }
    val swellFactor = remember { Animatable(0f) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    touchPos = down.position
                    coroutineScope.launch {
                        swellFactor.animateTo(1f, tween(150))
                    }
                    var current = down
                    while (current.pressed) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break
                        touchPos = change.position
                        current = change
                    }
                    coroutineScope.launch {
                        swellFactor.animateTo(0f, tween(450))
                        touchPos = null
                    }
                }
            }
    ) {
        drawRect(color = backgroundColor)

        val cellSize = 42.dp.toPx()
        val swellRadius = 140.dp.toPx()
        val maxSwell = 24.dp.toPx() * swellFactor.value

        val width = size.width
        val height = size.height
        val activeTouch = touchPos

        // 1. Draw tactile glowing radial aura under finger when active
        if (activeTouch != null && swellFactor.value > 0.01f) {
            val auraRadius = swellRadius * (0.8f + 0.2f * swellFactor.value)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        swellAuraColor.copy(alpha = swellAuraColor.alpha * swellFactor.value),
                        swellAuraColor.copy(alpha = swellAuraColor.alpha * 0.4f * swellFactor.value),
                        Color.Transparent
                    ),
                    center = activeTouch,
                    radius = auraRadius
                ),
                radius = auraRadius,
                center = activeTouch
            )
        }

        val cols = (ceil(width / cellSize).toInt() + 2)
        val rows = (ceil(height / cellSize).toInt() + 2)

        // 2. Pre-calculate displaced grid vertices using 3D cosine lens math
        val pts = Array(rows + 1) { r ->
            val basePy = (r - 1) * cellSize
            Array(cols + 1) { c ->
                val basePx = (c - 1) * cellSize
                var px = basePx
                var py = basePy

                if (activeTouch != null && maxSwell > 0.01f) {
                    val dx = basePx - activeTouch.x
                    val dy = basePy - activeTouch.y
                    val dist = sqrt(dx * dx + dy * dy)
                    if (dist < swellRadius && dist > 0.001f) {
                        val factor = cos((dist / swellRadius) * (Math.PI / 2.0)).toFloat()
                        val displacement = factor * factor * maxSwell
                        val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
                        px = basePx + cos(angle) * displacement
                        py = basePy + sin(angle) * displacement
                    }
                }
                Offset(px, py)
            }
        }

        // 3. Render horizontal curved lines connecting displaced vertices
        for (r in 0..rows) {
            val path = Path()
            path.moveTo(pts[r][0].x, pts[r][0].y)
            for (c in 1..cols) {
                path.lineTo(pts[r][c].x, pts[r][c].y)
            }
            drawPath(
                path = path,
                color = gridLineColor,
                style = Stroke(width = 1.2.dp.toPx())
            )
        }

        // 4. Render vertical curved lines connecting displaced vertices
        for (c in 0..cols) {
            val path = Path()
            path.moveTo(pts[0][c].x, pts[0][c].y)
            for (r in 1..rows) {
                path.lineTo(pts[r][c].x, pts[r][c].y)
            }
            drawPath(
                path = path,
                color = gridLineColor,
                style = Stroke(width = 1.2.dp.toPx())
            )
        }
    }
}
