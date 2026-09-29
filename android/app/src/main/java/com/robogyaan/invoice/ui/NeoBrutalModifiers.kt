package com.robogyaan.invoice.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Neo-Brutalist Modifier specified in system requirements.
 * Renders an asymmetric hard drop shadow offset with solid borders and rounded corners.
 */
fun Modifier.neoBrutal(
    backgroundColor: Color = Color(0xFFFFE600),
    borderColor: Color = Color.Black,
    borderWidth: Dp = 2.dp,
    shadowOffset: Dp = 4.dp,
    cornerRadius: Dp = 8.dp
): Modifier = this.drawBehind {
    val cornerRadiusPx = cornerRadius.toPx()
    val shadowOffsetPx = shadowOffset.toPx()
    val strokeWidthPx = borderWidth.toPx()
    val roundCorner = CornerRadius(cornerRadiusPx, cornerRadiusPx)

    // 1. Draw hard shadow offset to bottom-right
    if (shadowOffsetPx > 0f) {
        drawRoundRect(
            color = borderColor,
            topLeft = Offset(shadowOffsetPx, shadowOffsetPx),
            size = size,
            cornerRadius = roundCorner
        )
    }

    // 2. Draw solid background surface
    drawRoundRect(
        color = backgroundColor,
        topLeft = Offset.Zero,
        size = size,
        cornerRadius = roundCorner
    )

    // 3. Draw solid black border on top
    if (strokeWidthPx > 0f) {
        drawRoundRect(
            color = borderColor,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = roundCorner,
            style = Stroke(width = strokeWidthPx)
        )
    }
}

/**
 * Clickable Neo-Brutalist button modifier with micro-interaction translation and shadow reduction.
 */
fun Modifier.neoBrutalClickable(
    backgroundColor: Color = Color(0xFFFFE600),
    borderColor: Color = Color.Black,
    borderWidth: Dp = 2.dp,
    defaultShadowOffset: Dp = 4.dp,
    pressedShadowOffset: Dp = 1.dp,
    cornerRadius: Dp = 8.dp,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentShadow by animateDpAsState(
        targetValue = if (isPressed) pressedShadowOffset else defaultShadowOffset,
        label = "shadowAnim"
    )

    val translation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        label = "transAnim"
    )

    this
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(
                    x = translation.roundToPx(),
                    y = translation.roundToPx()
                )
            }
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
        .neoBrutal(
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            borderWidth = borderWidth,
            shadowOffset = currentShadow,
            cornerRadius = cornerRadius
        )
}
