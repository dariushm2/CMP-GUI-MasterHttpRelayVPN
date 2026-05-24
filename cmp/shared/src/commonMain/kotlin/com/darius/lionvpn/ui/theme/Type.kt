package com.darius.lionvpn.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with

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

val Typography = Typography(
    displayLarge = displayLg,
    headlineMedium = headlineMd,
    titleSmall = titleSm,
    bodyMedium = bodyMd,
    bodySmall = bodySm,
)
