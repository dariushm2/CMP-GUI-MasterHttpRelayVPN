package com.darius.lionvpn.ui.home

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Theme2 {
    // Cyber-Shield Systematic Color Palette
    val background = Color(0xFF0B1326)
    val surface = Color(0xFF0B1326)
    val surfaceDim = Color(0xFF0B1326)
    val surfaceBright = Color(0xFF31394D)
    
    val surfaceContainerLowest = Color(0xFF060E20)
    val surfaceContainerLow = Color(0xFF131B2E)
    val surfaceContainer = Color(0xFF171F33)
    val surfaceContainerHigh = Color(0xFF222A3D)
    val surfaceContainerHighest = Color(0xFF2D3449)
    
    val onSurface = Color(0xFFDAE2FD)
    val onSurfaceVariant = Color(0xFFC1C6D7)
    
    val outline = Color(0xFF8B90A0)
    val outlineVariant = Color(0xFF414755)
    
    val primary = Color(0xFFADC6FF)
    val onPrimary = Color(0xFF002E69)
    val primaryContainer = Color(0xFF4B8EFF)
    val onPrimaryContainer = Color(0xFF00285C)
    
    val secondary = Color(0xFF4EDEA3)
    val onSecondary = Color(0xFF003824)
    val secondaryContainer = Color(0xFF00A572)
    val onSecondaryContainer = Color(0xFF00311F)
    
    val tertiary = Color(0xFFFFB95F)
    val onTertiary = Color(0xFF472A00)
    val tertiaryContainer = Color(0xFFCA8100)
    val onTertiaryContainer = Color(0xFF3E2400)
    
    val error = Color(0xFFFFB4AB)
    val onError = Color(0xFF690005)
    val errorContainer = Color(0xFF93000A)
    val onErrorContainer = Color(0xFFFFDAD6)

    // Shapes
    val roundedSm = RoundedCornerShape(4.dp)
    val roundedDefault = RoundedCornerShape(8.dp)
    val roundedMd = RoundedCornerShape(12.dp)
    val roundedLg = RoundedCornerShape(16.dp)
    val roundedXl = RoundedCornerShape(24.dp)
    val roundedFull = RoundedCornerShape(9999.dp)

    // Spacing (Base unit = 8dp)
    val unit = 8.dp
    val containerPadding = 24.dp
    val gutter = 16.dp
    val stackSm = 8.dp
    val stackMd = 16.dp
    val stackLg = 32.dp

    // Typography style helper
    val displayLg = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    )
    val headlineMd = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp,
        letterSpacing = (-0.01).sp
    )
    val titleSm = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp
    )
    val bodyMd = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp
    )
    val bodySm = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp
    )
    val monoCode = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp
    )
    val labelCaps = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 16.sp,
        letterSpacing = 0.05.sp
    )
}

enum class LogType { Info, Success, Warn, Error }

