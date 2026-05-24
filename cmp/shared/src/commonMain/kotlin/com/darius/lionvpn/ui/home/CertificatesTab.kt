package com.darius.lionvpn.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.theme.*

@Composable
fun CertificatesTab(
    state: HomeState,
    onClick: (Event) -> Unit,
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
                    text = "HTTPS Certificate Management",
                    style = headlineMd.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurface
                    )
                )
                Text(
                    text = "In order for the app to be able to work, it needs to install a trusted CA certificate.",
                    style = bodySm.copy(color = onSurfaceVariant)
                )
            }
        }

        Divider(color = outlineVariant)

        // Status grid: Details Card & Actions Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(gutter)
        ) {
            // Manual Actions Card (Right, takes 1/3 of space)
            Card(
                shape = roundedLg,
                colors = CardDefaults.cardColors(
                    containerColor = surfaceContainerHighest
                ),
                border = borderStrokeGlass(),
                modifier = Modifier
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(gutter),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Manual Controls",
                        style = titleSm.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = onSurface)
                    )

                    // Install button
                    Button(
                        onClick = { onClick(Event.Certificate) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = secondary,
                            contentColor = onSecondary
                        ),
                        shape = roundedDefault,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DownloadDone,
                                    contentDescription = "Install Icon",
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Install HTTPS Cert",
                                    style = bodySm.copy(fontWeight = FontWeight.Bold, color = onSecondary)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Arrow Right",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Uninstall button
                    OutlinedButton(
                        onClick = { /* uninstall action mock */ },
                        shape = roundedDefault,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = error
                        ),
                        border = BorderStroke(1.dp, error.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = "Uninstall Icon",
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Uninstall Certificate",
                                    style = bodySm.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close indicator",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = "Note: Uninstallation will terminate active SSL inspection tunnels immediately.",
                        style = bodySm.copy(
                            fontSize = 11.sp,
                            color = onSurfaceVariant.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Light
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
