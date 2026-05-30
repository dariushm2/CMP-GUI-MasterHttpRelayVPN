package com.darius.lionvpn.ui.home.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.darius.lionvpn.ui.home.CertOperationType
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.containerPadding
import com.darius.lionvpn.ui.theme.error
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.outlineVariant
import com.darius.lionvpn.ui.theme.roundedDefault
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.surfaceContainerHighest
import kotlinx.coroutines.delay
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.cert_install_success
import lion_vpn.shared.generated.resources.cert_install_failure
import lion_vpn.shared.generated.resources.cert_uninstall_success
import lion_vpn.shared.generated.resources.cert_uninstall_failure
import lion_vpn.shared.generated.resources.cert_uninstall_android_note
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsTab(
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showConfigDialog by remember { mutableStateOf(false) }

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isSuccessToast by remember { mutableStateOf(true) }

    // Resolve strings in Composable scope to prevent runtime resource loading issues in coroutines
    val installSuccessMsg = stringResource(Res.string.cert_install_success)
    val installFailureMsg = stringResource(Res.string.cert_install_failure)
    val uninstallSuccessMsg = stringResource(Res.string.cert_uninstall_success)
    val uninstallFailureMsg = stringResource(Res.string.cert_uninstall_failure)
    val uninstallAndroidNoteMsg = stringResource(Res.string.cert_uninstall_android_note)

    LaunchedEffect(state.certOperationResult) {
        state.certOperationResult?.let { result ->
            isSuccessToast = result.isSuccess
            toastMessage = when (result.type) {
                CertOperationType.INSTALL -> {
                    if (result.isSuccess) installSuccessMsg else installFailureMsg
                }
                CertOperationType.UNINSTALL -> {
                    if (result.timestamp == -1L) {
                        uninstallAndroidNoteMsg
                    } else {
                        if (result.isSuccess) uninstallSuccessMsg else uninstallFailureMsg
                    }
                }
            }
            showToast = true
            delay(2000)
            showToast = false
            onClick(Event.ClearCertResult)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(containerPadding),
            verticalArrangement = Arrangement.spacedBy(gutter)
        ) {
            SettingsHeader()
            
            Divider(color = outlineVariant)

            GeneralSettingsSection(
                language = state.language,
                onLanguageChange = { onClick(Event.ChangeLanguage(it)) }
            )

            ConnectionSettingsSection(
                onOpenConfig = { showConfigDialog = true }
            )

            SecuritySettingsSection(
                state = state,
                onInstall = { onClick(Event.InstallCertificate) },
                onUninstall = { onClick(Event.UninstallCertificate) }
            )
        }

        // Custom Toast overlay
        AnimatedVisibility(
            visible = showToast,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            ToastNotificationCard(
                message = toastMessage,
                isSuccess = isSuccessToast
            )
        }
    }

    if (showConfigDialog) {
        EditConfigDialog(
            state = state,
            onClick = onClick,
            onDismiss = { showConfigDialog = false }
        )
    }
}

@Composable
private fun ToastNotificationCard(
    message: String,
    isSuccess: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = surfaceContainerHighest.copy(alpha = 0.85f)),
        shape = roundedDefault,
        border = borderStrokeGlass(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = if (isSuccess) "Success" else "Error",
                tint = if (isSuccess) secondary else error,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = message,
                style = bodySm.copy(fontWeight = FontWeight.SemiBold, color = onSurface)
            )
        }
    }
}
