package com.darius.lionvpn.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.theme.*
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

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
                // Interactive Diagram Card
                Card(
                    shape = roundedLg,
                    colors = CardDefaults.cardColors(
                        containerColor = surfaceContainerLowest
                    ),
                    border = borderStrokeGlass(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(gutter),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "نحوه انتقال و عبور ترافیک (Traffic Flow Architecture)",
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
                                // Step 1: Device (Client)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .background(primary.copy(alpha = 0.1f), CircleShape)
                                            .border(1.5.dp, primary.copy(alpha = 0.4f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Device",
                                            tint = primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Text(
                                        text = "دستگاه شما",
                                        style = bodySm.copy(fontWeight = FontWeight.Bold, color = onSurface)
                                    )
                                    Text(
                                        text = "بسته‌بندی ترافیک\n(Repackage)",
                                        style = bodySm.copy(fontSize = 10.sp, color = onSurfaceVariant)
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Flow direction",
                                        tint = secondary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Flow direction",
                                        tint = secondary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // Step 2: Google Servers (Relay)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .background(secondary.copy(alpha = 0.1f), CircleShape)
                                            .border(1.5.dp, secondary.copy(alpha = 0.4f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudQueue,
                                            contentDescription = "Google Apps Script",
                                            tint = secondary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Text(
                                        text = "رله گوگل",
                                        style = bodySm.copy(fontWeight = FontWeight.Bold, color = onSurface)
                                    )
                                    Text(
                                        text = "بازگشایی و دریافت\n(Depackage)",
                                        style = bodySm.copy(fontSize = 10.sp, color = onSurfaceVariant)
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Flow direction",
                                        tint = secondary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Flow direction",
                                        tint = secondary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // Step 3: Original Website
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .background(tertiary.copy(alpha = 0.1f), CircleShape)
                                            .border(1.5.dp, tertiary.copy(alpha = 0.4f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Language,
                                            contentDescription = "Destination Site",
                                            tint = tertiary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Text(
                                        text = "سایت مقصد",
                                        style = bodySm.copy(fontWeight = FontWeight.Bold, color = onSurface)
                                    )
                                    Text(
                                        text = "دریافت پاسخ\n(Destination)",
                                        style = bodySm.copy(fontSize = 10.sp, color = onSurfaceVariant)
                                    )
                                }
                            }
                        }
                    }
                }

                // Description Card
                Card(
                    shape = roundedLg,
                    colors = CardDefaults.cardColors(
                        containerColor = surfaceContainerLowest
                    ),
                    border = borderStrokeGlass(),
                    modifier = Modifier.fillMaxWidth()
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
                                contentDescription = "About App",
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

                // Features/Benefits Card
                Card(
                    shape = roundedLg,
                    colors = CardDefaults.cardColors(
                        containerColor = surfaceContainerLowest
                    ),
                    border = borderStrokeGlass(),
                    modifier = Modifier.fillMaxWidth()
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
                                contentDescription = "App Benefits",
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

                // Google Apps Script Rate Limits Card
                Card(
                    shape = roundedLg,
                    colors = CardDefaults.cardColors(
                        containerColor = surfaceContainerLowest
                    ),
                    border = borderStrokeGlass(),
                    modifier = Modifier.fillMaxWidth()
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
                                contentDescription = "Rate Limits",
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
        }
    }
}
