package com.darius.lionvpn.ui.home.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.theme.containerPadding
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.outlineVariant

@Composable
fun SettingsTab(
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showConfigDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
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
            onInstall = { onClick(Event.InstallCertificate) },
            onUninstall = { onClick(Event.UninstallCertificate) }
        )
    }

    if (showConfigDialog) {
        EditConfigDialog(
            state = state,
            onClick = onClick,
            onDismiss = { showConfigDialog = false }
        )
    }
}
