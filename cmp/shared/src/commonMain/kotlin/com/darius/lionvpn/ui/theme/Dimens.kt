package com.darius.lionvpn.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun borderStrokeGlass() = BorderStroke(1.dp, Color(0x33DAE2FD)) // Outline-variant alpha border

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