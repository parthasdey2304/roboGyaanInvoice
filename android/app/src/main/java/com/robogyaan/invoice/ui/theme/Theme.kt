package com.robogyaan.invoice.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NeoYellow,
    onPrimary = NeoBlack,
    secondary = NeoOrange,
    onSecondary = NeoBlack,
    tertiary = NeoDarkGray,
    background = Color(0xFFFDFBF7),
    surface = NeoSurface,
    onSurface = NeoBlack
)

private val DarkColorScheme = darkColorScheme(
    primary = NeoYellow,
    onPrimary = NeoBlack,
    secondary = NeoOrange,
    onSecondary = NeoBlack,
    tertiary = NeoDarkGray,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E22),
    onSurface = Color.White
)

@Composable
fun RoboGyaanInvoiceTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) Color.Black.toArgb() else NeoYellow.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
