package com.darius.lionvpn.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LogTerminal(
    log: List<String>,
    modifier: Modifier = Modifier
) {
    Text(
        text = log.takeLast(5).joinToString("\n"),
        maxLines = 5,
        minLines = 5,
        color = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(8.dp)
    )
}
