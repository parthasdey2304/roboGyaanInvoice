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
    Font(R.font.virgil, FontWeight.Medium),
    Font(R.font.virgil, FontWeight.SemiBold),
    Font(R.font.virgil, FontWeight.Bold)
)

val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
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
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = NeoBlack
    ),
    titleMedium = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = NeoBlack
    ),
    bodyLarge = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = NeoBlack
    ),
    bodyMedium = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = NeoBlack
    ),
    labelLarge = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = NeoBlack
    ),
    labelSmall = TextStyle(
        fontFamily = VirgilFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        color = NeoBlack
    )
)
