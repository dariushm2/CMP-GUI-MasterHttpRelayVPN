package com.darius.lionvpn.ui.home.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.theme.error
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.labelCaps
import com.darius.lionvpn.ui.theme.monoCode
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.surfaceContainerLow
import com.darius.lionvpn.ui.theme.surfaceContainerLowest
import com.darius.lionvpn.ui.theme.tertiary
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.clear_logs_desc
import lion_vpn.shared.generated.resources.logs_terminal_desc
import lion_vpn.shared.generated.resources.system_logs
import org.jetbrains.compose.resources.stringResource

enum class LogType { Info, Success, Warn, Error }

private fun parseLogType(line: String): Pair<String, LogType> {
    return when {
        line.contains("SUCCESS", ignoreCase = true) || line.contains("connected", ignoreCase = true) -> line to LogType.Success
        line.contains("WARN", ignoreCase = true) || line.contains("rerouting", ignoreCase = true) -> line to LogType.Warn
        line.contains("ERR", ignoreCase = true) || line.contains("failed", ignoreCase = true) -> line to LogType.Error
        else -> line to LogType.Info
    }
}

@Composable
fun ConsoleHeader(
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceContainerLow)
            .padding(horizontal = gutter, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = stringResource(Res.string.logs_terminal_desc),
                tint = primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = stringResource(Res.string.system_logs),
                style = labelCaps.copy(color = onSurfaceVariant)
            )
        }
        IconButton(
            onClick = onClearClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.clear_logs_desc),
                tint = onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun LogEntry(
    text: String,
    type: LogType,
    modifier: Modifier = Modifier
) {
    val textColor = when (type) {
        LogType.Info -> onSurfaceVariant.copy(alpha = 0.8f)
        LogType.Success -> secondary
        LogType.Warn -> tertiary
        LogType.Error -> error
    }
    Text(
        text = text,
        style = monoCode.copy(color = textColor, fontSize = 12.sp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun ConsoleBody(
    logs: List<String>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        SelectionContainer(modifier = modifier) {
            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(16.dp)
            ) {
                items(logs) { log ->
                    val (formattedText, type) = parseLogType(log)
                    LogEntry(formattedText, type = type)
                }
            }
        }
    }
}

@Composable
fun TerminalLogConsole(
    logs: List<String>,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) lazyListState.scrollToItem(logs.lastIndex)
    }

    Card(
        shape = roundedLg,
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowest),
        border = BorderStroke(1.dp, Color(0x33DAE2FD)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ConsoleHeader(onClearClick = onClearClick)
            ConsoleBody(logs = logs, lazyListState = lazyListState, modifier = Modifier.weight(1f))
        }
    }
}
