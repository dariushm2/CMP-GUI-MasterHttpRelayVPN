package com.darius.lionvpn.ui.home.dashboard

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.home.ConnectionState
import com.darius.lionvpn.ui.theme.labelCaps
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.outlineVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.surfaceContainerHighest
import com.darius.lionvpn.ui.theme.tertiary
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.no_config
import lion_vpn.shared.generated.resources.power_vpn_button_desc
import org.jetbrains.compose.resources.stringResource

private fun infiniteTransitionSpec(duration: Int) = infiniteRepeatable<Float>(
    animation = tween(duration, easing = LinearEasing),
    repeatMode = RepeatMode.Restart
)

@Composable
fun HeroCanvasBackground(
    vpnState: ConnectionState,
    flowProgress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f

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

        val nodes = listOf(
            Offset(width * 0.15f, height * 0.25f),
            Offset(width * 0.85f, height * 0.22f),
            Offset(width * 0.22f, height * 0.78f),
            Offset(width * 0.82f, height * 0.75f),
            Offset(width * 0.5f, height * 0.88f)
        )

        nodes.forEach { nodePos ->
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
}

@Composable
fun HeroPowerButton(
    vpnState: ConnectionState,
    isConnectEnabled: Boolean,
    address: String,
    pulseProgress: Float,
    pulseAlpha: Float,
    onConnectToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
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
                        fontWeight = FontWeight.Bold,
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

@Composable
fun ConnectionHeroCard(
    vpnState: ConnectionState,
    isConnectEnabled: Boolean,
    address: String,
    onConnectToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()

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
    val flowProgress by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 1.0f,
        animationSpec = infiniteTransitionSpec(4000)
    )

    Card(
        modifier = modifier,
        shape = roundedLg,
        colors = CardDefaults.cardColors(containerColor = Color(0x1F1E293B)),
        border = BorderStroke(1.dp, Color(0x33DAE2FD))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            HeroCanvasBackground(vpnState = vpnState, flowProgress = flowProgress)
            HeroPowerButton(
                vpnState = vpnState,
                isConnectEnabled = isConnectEnabled,
                address = address,
                pulseProgress = pulseProgress,
                pulseAlpha = pulseAlpha,
                onConnectToggle = onConnectToggle
            )
        }
    }
}
