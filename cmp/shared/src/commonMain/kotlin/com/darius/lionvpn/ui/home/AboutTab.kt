package com.darius.lionvpn.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.theme.bodyMd
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.containerPadding
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.headlineMd
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.outlineVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.surfaceContainerLowest
import com.darius.lionvpn.ui.theme.tertiary
import com.darius.lionvpn.ui.theme.titleSm
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.about_desc_benefits_icon
import lion_vpn.shared.generated.resources.about_desc_destination_icon
import lion_vpn.shared.generated.resources.about_desc_device_icon
import lion_vpn.shared.generated.resources.about_desc_flow_icon
import lion_vpn.shared.generated.resources.about_desc_info_icon
import lion_vpn.shared.generated.resources.about_desc_relay_icon
import lion_vpn.shared.generated.resources.about_desc_warning_icon
import lion_vpn.shared.generated.resources.about_description
import lion_vpn.shared.generated.resources.about_diagram_title
import lion_vpn.shared.generated.resources.about_feature_1
import lion_vpn.shared.generated.resources.about_feature_2
import lion_vpn.shared.generated.resources.about_feature_3
import lion_vpn.shared.generated.resources.about_features_title
import lion_vpn.shared.generated.resources.about_limits_description
import lion_vpn.shared.generated.resources.about_limits_title
import lion_vpn.shared.generated.resources.about_project_desc_part1
import lion_vpn.shared.generated.resources.about_project_desc_part2
import lion_vpn.shared.generated.resources.about_project_desc_part3
import lion_vpn.shared.generated.resources.about_project_desc_part4
import lion_vpn.shared.generated.resources.about_project_github_desc
import lion_vpn.shared.generated.resources.about_project_gui_fork
import lion_vpn.shared.generated.resources.about_project_title
import lion_vpn.shared.generated.resources.about_project_upstream
import lion_vpn.shared.generated.resources.about_step_destination
import lion_vpn.shared.generated.resources.about_step_destination_desc
import lion_vpn.shared.generated.resources.about_step_device
import lion_vpn.shared.generated.resources.about_step_device_desc
import lion_vpn.shared.generated.resources.about_step_relay
import lion_vpn.shared.generated.resources.about_step_relay_desc
import lion_vpn.shared.generated.resources.about_subtitle
import lion_vpn.shared.generated.resources.about_title
import lion_vpn.shared.generated.resources.tab_about
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutTab(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(containerPadding),
        verticalArrangement = Arrangement.spacedBy(gutter)
    ) {
        // Tab Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(Res.string.tab_about),
                    style = headlineMd.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurface
                    )
                )
                Text(
                    text = stringResource(Res.string.about_subtitle),
                    style = bodySm.copy(color = onSurfaceVariant)
                )
            }
        }

        HorizontalDivider(color = outlineVariant)

        SelectionContainer {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(gutter)
            ) {
                TrafficFlowDiagramCard()
                DescriptionCard()
                FeaturesCard()
                LimitsCard()
                ProjectCard()
            }
        }
    }
}

@Composable
private fun TrafficFlowDiagramCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = roundedLg,
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowest),
        border = borderStrokeGlass(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(gutter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.about_diagram_title),
                style = titleSm.copy(fontWeight = FontWeight.Bold, color = primary),
                modifier = Modifier.align(Alignment.Start)
            )

            // Visual Flow Diagrams (LTR flow representation)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlowStepNode(
                        title = stringResource(Res.string.about_step_device),
                        desc = stringResource(Res.string.about_step_device_desc),
                        icon = Icons.Default.Lock,
                        iconDesc = stringResource(Res.string.about_desc_device_icon),
                        color = primary
                    )

                    FlowArrowIndicator()

                    FlowStepNode(
                        title = stringResource(Res.string.about_step_relay),
                        desc = stringResource(Res.string.about_step_relay_desc),
                        icon = Icons.Default.CloudQueue,
                        iconDesc = stringResource(Res.string.about_desc_relay_icon),
                        color = secondary
                    )

                    FlowArrowIndicator()

                    FlowStepNode(
                        title = stringResource(Res.string.about_step_destination),
                        desc = stringResource(Res.string.about_step_destination_desc),
                        icon = Icons.Default.Language,
                        iconDesc = stringResource(Res.string.about_desc_destination_icon),
                        color = tertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun FlowStepNode(
    title: String,
    desc: String,
    icon: ImageVector,
    iconDesc: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(color.copy(alpha = 0.1f), CircleShape)
                .border(1.5.dp, color.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDesc,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = title,
            style = bodySm.copy(fontWeight = FontWeight.Bold, color = onSurface)
        )
        Text(
            text = desc,
            style = bodySm.copy(fontSize = 10.sp, color = onSurfaceVariant)
        )
    }
}

@Composable
private fun FlowArrowIndicator(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = stringResource(Res.string.about_desc_flow_icon),
            tint = secondary.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.about_desc_flow_icon),
            tint = secondary.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun DescriptionCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = roundedLg,
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowest),
        border = borderStrokeGlass(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(gutter),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(Res.string.about_desc_info_icon),
                    tint = primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(Res.string.about_title),
                    style = titleSm.copy(fontWeight = FontWeight.Bold, color = onSurface)
                )
            }

            Text(
                text = stringResource(Res.string.about_description),
                style = bodyMd.copy(color = onSurfaceVariant, lineHeight = 24.sp)
            )
        }
    }
}

@Composable
private fun FeaturesCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = roundedLg,
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowest),
        border = borderStrokeGlass(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(gutter),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = stringResource(Res.string.about_desc_benefits_icon),
                    tint = secondary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(Res.string.about_features_title),
                    style = titleSm.copy(fontWeight = FontWeight.Bold, color = onSurface)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.about_feature_1),
                    style = bodyMd.copy(color = onSurfaceVariant)
                )
                Text(
                    text = stringResource(Res.string.about_feature_2),
                    style = bodyMd.copy(color = onSurfaceVariant)
                )
                Text(
                    text = stringResource(Res.string.about_feature_3),
                    style = bodyMd.copy(color = onSurfaceVariant)
                )
            }
        }
    }
}

@Composable
private fun LimitsCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = roundedLg,
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowest),
        border = borderStrokeGlass(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(gutter),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = stringResource(Res.string.about_desc_warning_icon),
                    tint = tertiary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(Res.string.about_limits_title),
                    style = titleSm.copy(fontWeight = FontWeight.Bold, color = onSurface)
                )
            }

            Text(
                text = stringResource(Res.string.about_limits_description),
                style = bodyMd.copy(color = onSurfaceVariant, lineHeight = 24.sp)
            )
        }
    }
}

@Composable
private fun ProjectCard(
    modifier: Modifier = Modifier
) {
    val upstreamUrl = "https://github.com/masterking32/MasterHttpRelayVPN"
    val forkUrl = "https://github.com/dariushm2/CMP-GUI-MasterHttpRelayVPN"

    Card(
        shape = roundedLg,
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowest),
        border = borderStrokeGlass(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(gutter),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = stringResource(Res.string.about_project_github_desc),
                    tint = primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(Res.string.about_project_title),
                    style = titleSm.copy(fontWeight = FontWeight.Bold, color = onSurface)
                )
            }

            ProjectClickableDescription(upstreamUrl = upstreamUrl, forkUrl = forkUrl)
        }
    }
}

@Composable
private fun ProjectClickableDescription(
    upstreamUrl: String,
    forkUrl: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val descPart1 = stringResource(Res.string.about_project_desc_part1)
    val descPart2 = stringResource(Res.string.about_project_desc_part2)
    val descPart3 = stringResource(Res.string.about_project_desc_part3)
    val descPart4 = stringResource(Res.string.about_project_desc_part4)
    val guiForkLabel = stringResource(Res.string.about_project_gui_fork)
    val upstreamLabel = stringResource(Res.string.about_project_upstream)

    val annotatedString = buildAnnotatedString {
        append(descPart1)

        pushStringAnnotation(tag = "UPSTREAM", annotation = upstreamUrl)
        withStyle(style = SpanStyle(color = primary, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
            append("MasterHttpRelayVPN")
        }
        pop()

        append(descPart2)

        pushStringAnnotation(tag = "FORK", annotation = forkUrl)
        withStyle(style = SpanStyle(color = primary, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
            append(guiForkLabel)
        }
        pop()

        append(descPart3)

        pushStringAnnotation(tag = "UPSTREAM", annotation = upstreamUrl)
        withStyle(style = SpanStyle(color = primary, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
            append(upstreamLabel)
        }
        pop()

        append(descPart4)
    }

    ClickableText(
        text = annotatedString,
        style = bodyMd.copy(color = onSurfaceVariant, lineHeight = 24.sp),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "UPSTREAM", start = offset, end = offset).firstOrNull()?.let { annotation ->
                uriHandler.openUri(annotation.item)
            }
            annotatedString.getStringAnnotations(tag = "FORK", start = offset, end = offset).firstOrNull()?.let { annotation ->
                uriHandler.openUri(annotation.item)
            }
        },
        modifier = modifier
    )
}
