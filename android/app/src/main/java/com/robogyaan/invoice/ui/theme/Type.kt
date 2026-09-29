package com.robogyaan.invoice.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.robogyaan.invoice.R

val VirgilFontFamily = FontFamily(
    Font(R.font.virgil, FontWeight.Normal),
    Font(R.font.virgil, FontWeight.Bold)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = NeoBlack
    ),
    titleLarge = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = NeoBlack
    ),
    titleMedium = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = NeoBlack
    ),
    bodyLarge = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        color = NeoBlack
    ),
    bodyMedium = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = NeoBlack
    ),
    labelLarge = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = NeoBlack
    ),
    labelSmall = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        color = NeoBlack
    )
)
