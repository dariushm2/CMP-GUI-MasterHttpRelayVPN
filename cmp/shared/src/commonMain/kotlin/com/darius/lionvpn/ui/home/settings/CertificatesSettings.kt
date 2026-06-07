package com.darius.lionvpn.ui.home.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.theme.bodyMd
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.error
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.onSecondary
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedDefault
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.surfaceContainerHighest
import com.darius.lionvpn.ui.theme.titleSm
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.settings_section_security
import lion_vpn.shared.generated.resources.settings_item_cert_title
import lion_vpn.shared.generated.resources.settings_item_cert_desc
import lion_vpn.shared.generated.resources.install_https_cert
import lion_vpn.shared.generated.resources.uninstall_certificate
import lion_vpn.shared.generated.resources.uninstallation_note
import lion_vpn.shared.generated.resources.cert_status_trusted
import lion_vpn.shared.generated.resources.cert_status_not_trusted
import org.jetbrains.compose.resources.stringResource

@Composable
fun SecuritySettingsSection(
    state: HomeState,
    onInstall: () -> Unit,
    onUninstall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.settings_section_security),
            style = titleSm.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primary)
        )
        CertificatesSettingsCard(
            state = state,
            onInstall = onInstall,
            onUninstall = onUninstall
        )
    }
}

@Composable
private fun CertificatesSettingsCard(
    state: HomeState,
    onInstall: () -> Unit,
    onUninstall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = roundedLg,
        colors = CardDefaults.cardColors(containerColor = surfaceContainerHighest),
        border = borderStrokeGlass(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(gutter),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(Res.string.settings_item_cert_title),
                    style = bodyMd.copy(fontWeight = FontWeight.Bold, color = onSurface)
                )
                Text(
                    text = stringResource(Res.string.settings_item_cert_desc),
                    style = bodySm.copy(fontSize = 12.sp, color = onSurfaceVariant)
                )
            }

            // Trust Status Badge (Dynamic based on trust detection)
            val isTrusted = state.isCertTrusted
            val statusText = if (isTrusted) {
                stringResource(Res.string.cert_status_trusted)
            } else {
                stringResource(Res.string.cert_status_not_trusted)
            }
            val badgeColor = if (isTrusted) secondary else error
            val badgeBg = if (isTrusted) secondary.copy(alpha = 0.12f) else error.copy(alpha = 0.12f)
            val statusIcon = if (isTrusted) Icons.Default.CheckCircle else Icons.Default.Warning

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(badgeBg, roundedDefault)
                    .border(BorderStroke(1.dp, badgeColor.copy(alpha = 0.35f)), roundedDefault)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = "Status",
                    tint = badgeColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = statusText,
                    style = bodySm.copy(fontWeight = FontWeight.SemiBold, color = onSurface)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val showInstall = state.isAndroid || !state.isCertTrusted
                val showUninstall = state.isAndroid || state.isCertTrusted

                if (showInstall) {
                    Button(
                        onClick = onInstall,
                        enabled = !state.isCertBusy,
                        colors = ButtonDefaults.buttonColors(containerColor = secondary, contentColor = onSecondary),
                        shape = roundedDefault,
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.isCertBusy) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = onSecondary
                                )
                            } else {
                                Icon(Icons.Default.DownloadDone, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                            Text(stringResource(Res.string.install_https_cert), style = bodySm.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                if (showUninstall) {
                    OutlinedButton(
                        onClick = onUninstall,
                        enabled = !state.isCertBusy,
                        shape = roundedDefault,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = error),
                        border = BorderStroke(1.dp, error.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.isCertBusy) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = error
                                )
                            } else {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                            Text(stringResource(Res.string.uninstall_certificate), style = bodySm.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
            Text(
                text = stringResource(Res.string.uninstallation_note),
                style = bodySm.copy(
                    fontSize = 11.sp,
                    color = onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Light,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
