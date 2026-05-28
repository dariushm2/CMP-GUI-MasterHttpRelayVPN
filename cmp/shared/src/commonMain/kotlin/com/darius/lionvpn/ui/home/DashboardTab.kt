package com.darius.lionvpn.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.theme.*
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.*
import kotlinx.serialization.json.*

@Composable
fun DashboardTab(
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    // Check if configuration is active
    val activeConfig = if (state.selectedConfigIndex in state.savedConfigs.indices) {
        state.savedConfigs[state.selectedConfigIndex]
    } else {
        null
    }
    
    val isConnectEnabled = activeConfig != null && activeConfig.id.isNotEmpty() && activeConfig.key.isNotEmpty()
    
    // Parse raw configuration json reactively to get listen_host and http_port in a single safe block
    val address = remember(state.rawConfigJson) {
        try {
            val obj = Json.parseToJsonElement(state.rawConfigJson).jsonObject
            val host = obj["listen_host"]?.jsonPrimitive?.content ?: "127.0.0.1"
            val port = obj["http_port"]?.jsonPrimitive?.intOrNull ?: 8085
            "$host:$port"
        } catch (e: Exception) {
            "127.0.0.1:8085"
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val showSideBySide = maxHeight < 480.dp

        if (!showSideBySide) {
            // Balanced non-scrollable Column for Portrait Mobile (no scroll conflicts!)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(containerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(gutter)
            ) {
                ConnectionHeroCard(
                    vpnState = state.connectionState,
                    isConnectEnabled = isConnectEnabled,
                    address = address,
                    onConnectToggle = { if (isConnectEnabled) onClick(Event.Connect) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .heightIn(max = 280.dp) // Capped at 280dp on tall screens
                )

                TerminalLogConsole(
                    logs = state.log,
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Balanced logs space filling the rest
                )
            }
        } else {
            // Side-by-side Row for Wide Screens OR Compact Height Screens (Desktop, Tablets, Landscape Phones)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(containerPadding),
                horizontalArrangement = Arrangement.spacedBy(gutter),
                verticalAlignment = Alignment.Top
            ) {
                // Connection Hero Card on the Left (adapts height but caps at 320dp)
                ConnectionHeroCard(
                    vpnState = state.connectionState,
                    isConnectEnabled = isConnectEnabled,
                    address = address,
                    onConnectToggle = { if (isConnectEnabled) onClick(Event.Connect) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .heightIn(max = 320.dp)
                )

                // Terminal Log Console Card on the Right (fills rest of height)
                TerminalLogConsole(
                    logs = state.log,
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun ConnectionHeroCard(
    vpnState: ConnectionState,
    isConnectEnabled: Boolean,
    address: String,
    onConnectToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Infinite transition for particle flow and pulsing glows
    val infiniteTransition = rememberInfiniteTransition()
    
    // Pulsing circle ring scale and alpha
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteTransitionSpec(3000)
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.0f,
        animationSpec = infiniteTransitionSpec(3000)
    )

    // Particle flow progress along lines
    val flowProgress by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 1.0f,
        animationSpec = infiniteTransitionSpec(4000)
    )

    Card(
        modifier = modifier,
        shape = roundedLg,
        colors = CardDefaults.cardColors(
            containerColor = Color(0x1F1E293B) // 70% opacity in slate-800 context
        ),
        border = borderStrokeGlass()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Animated Global Network Map drawn procedurally
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val centerX = width / 2f
                val centerY = height / 2f

                // Draw technical grid lines / circles
                drawCircle(
                    color = outlineVariant.copy(alpha = 0.15f),
                    radius = 180.dp.toPx(),
                    style = Stroke(width = 1.dp.toPx())
                )
                drawCircle(
                    color = outlineVariant.copy(alpha = 0.1f),
                    radius = 280.dp.toPx(),
                    style = Stroke(width = 1.dp.toPx())
                )

                // Define global nodes (relative coordinates)
                val nodes = listOf(
                    Offset(width * 0.15f, height * 0.25f) to "NYC-01",
                    Offset(width * 0.85f, height * 0.22f) to "FRA-04",
                    Offset(width * 0.22f, height * 0.78f) to "SGP-02",
                    Offset(width * 0.82f, height * 0.75f) to "TKY-01",
                    Offset(width * 0.5f, height * 0.88f) to "SYD-03"
                )

                // Draw connection lines to center and between nodes
                nodes.forEach { (nodePos, _) ->
                    // Line to central VPN engine
                    drawLine(
                        color = when (vpnState) {
                            ConnectionState.CONNECTED -> secondary.copy(alpha = 0.2f)
                            ConnectionState.CONNECTING -> tertiary.copy(alpha = 0.2f)
                            ConnectionState.DISCONNECTED -> outlineVariant.copy(alpha = 0.15f)
                        },
                        start = nodePos,
                        end = Offset(centerX, centerY),
                        strokeWidth = 1.5.dp.toPx()
                    )

                    // Draw animated flows along lines if connected
                    if (vpnState != ConnectionState.DISCONNECTED) {
                        val dx = centerX - nodePos.x
                        val dy = centerY - nodePos.y
                        val currentX = nodePos.x + dx * flowProgress
                        val currentY = nodePos.y + dy * flowProgress
                        drawCircle(
                            color = if (vpnState == ConnectionState.CONNECTING) tertiary else secondary,
                            radius = 3.dp.toPx(),
                            center = Offset(currentX, currentY)
                        )
                    }

                    // Node dot
                    drawCircle(
                        color = when (vpnState) {
                            ConnectionState.CONNECTED -> secondary
                            ConnectionState.CONNECTING -> tertiary
                            ConnectionState.DISCONNECTED -> outlineVariant
                        },
                        radius = 4.dp.toPx(),
                        center = nodePos
                    )
                }
            }

            // Central Interaction elements
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulse Ring Layer when active
                    if (vpnState != ConnectionState.DISCONNECTED) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(pulseProgress)
                                .background(
                                    (if (vpnState == ConnectionState.CONNECTING) tertiary else secondary).copy(alpha = pulseAlpha),
                                    CircleShape
                                )
                        )
                    }

                    // Outer connection glow bloom when connected
                    val glowModifier = if (vpnState != ConnectionState.DISCONNECTED) {
                        Modifier.drawBehind {
                            drawCircle(
                                color = (if (vpnState == ConnectionState.CONNECTING) tertiary else secondary).copy(alpha = 0.25f),
                                radius = 96.dp.toPx()
                            )
                        }
                    } else {
                        Modifier
                    }

                    // Main Power Button circular border & body
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .then(glowModifier)
                            .background(surfaceContainerHighest, CircleShape)
                            .border(
                                width = 4.dp,
                                color = when (vpnState) {
                                    ConnectionState.CONNECTED -> secondary
                                    ConnectionState.CONNECTING -> tertiary
                                    ConnectionState.DISCONNECTED -> if (isConnectEnabled) primary else outlineVariant
                                },
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .clickable(enabled = isConnectEnabled) { onConnectToggle() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = stringResource(Res.string.power_vpn_button_desc),
                                tint = when (vpnState) {
                                    ConnectionState.CONNECTED -> secondary
                                    ConnectionState.CONNECTING -> tertiary
                                    ConnectionState.DISCONNECTED -> if (isConnectEnabled) primary else onSurfaceVariant.copy(alpha = 0.4f)
                                },
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = when (vpnState) {
                                    ConnectionState.CONNECTED -> address
                                    ConnectionState.CONNECTING -> ""
                                    ConnectionState.DISCONNECTED -> if (isConnectEnabled) "" else stringResource(Res.string.no_config)
                                },
                                style = labelCaps.copy(
                                    fontSize = 11.sp,
                                    color = when (vpnState) {
                                        ConnectionState.CONNECTED -> secondary
                                        ConnectionState.CONNECTING -> tertiary
                                        ConnectionState.DISCONNECTED -> if (isConnectEnabled) onSurfaceVariant else onSurfaceVariant.copy(alpha = 0.4f)
                                    },
                                    letterSpacing = 2.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TerminalLogConsole(
    logs: List<String>,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) lazyListState.scrollToItem(logs.lastIndex)
    }
    Card(
        shape = roundedLg,
        colors = CardDefaults.cardColors(
            containerColor = surfaceContainerLowest
        ),
        border = borderStrokeGlass(),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header bar
            Row(
                modifier = Modifier
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
                    onClick = { onClick(Event.ClearLogs) },
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

            // Scrollable terminal content

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                SelectionContainer {
                    LazyColumn(
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(16.dp)
                    ) {
                        // If logs are empty/null, display the mock startup logs from the design
                        items(logs) { log ->
                            val (formattedText, type) = parseLogType(log)
                            LogEntry(formattedText, type = type)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogEntry(
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
        style = monoCode.copy(
            color = textColor,
            fontSize = 12.sp
        ),
        modifier = modifier.fillMaxWidth()
    )
}

enum class LogType { Info, Success, Warn, Error }

// Simple parsing helper for custom coloring logs
private fun parseLogType(line: String): Pair<String, LogType> {
    return when {
        line.contains("SUCCESS", ignoreCase = true) || line.contains("connected", ignoreCase = true) -> line to LogType.Success
        line.contains("WARN", ignoreCase = true) || line.contains("rerouting", ignoreCase = true) -> line to LogType.Warn
        line.contains("ERR", ignoreCase = true) || line.contains("failed", ignoreCase = true) -> line to LogType.Error
        else -> line to LogType.Info
    }
}

// Utility styling functions
@Composable
fun borderStrokeGlass() = BorderStroke(1.dp, Color(0x33DAE2FD)) // Outline-variant alpha border

@Composable
private fun infiniteTransitionSpec(duration: Int) = infiniteRepeatable<Float>(
    animation = tween(duration, easing = LinearEasing),
    repeatMode = RepeatMode.Restart
)
