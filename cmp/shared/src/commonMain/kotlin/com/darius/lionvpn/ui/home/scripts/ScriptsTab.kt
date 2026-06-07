package com.darius.lionvpn.ui.home.scripts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.model.SavedConfig
import com.darius.lionvpn.ui.theme.containerPadding
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.headlineMd
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.onSecondary
import com.darius.lionvpn.ui.theme.roundedDefault
import com.darius.lionvpn.ui.theme.surfaceContainerLow
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.titleSm
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.surfaceContainerHighest
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.scripts_management
import lion_vpn.shared.generated.resources.add_script_icon_desc
import lion_vpn.shared.generated.resources.add_script
import lion_vpn.shared.generated.resources.setup_instructions_title
import lion_vpn.shared.generated.resources.setup_instructions_click_to_view
import lion_vpn.shared.generated.resources.copied_toast

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ScriptsTab(
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isAddDialogVisible by remember { mutableStateOf(false) }
    var isInstructionsDialogVisible by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }

    LaunchedEffect(showToast) {
        if (showToast) {
            delay(1500)
            showToast = false
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
            ScriptsTabHeader(onAddClick = { isAddDialogVisible = true })

            SetupInstructionsBanner(
                language = state.language,
                onClick = { isInstructionsDialogVisible = true }
            )

            ScriptsList(
                state = state,
                onClick = onClick,
                onCopied = { showToast = true }
            )

            if (isAddDialogVisible) {
                AddScriptDialog(
                    isVpnRunning = state.isVpnRunning,
                    onDismiss = { isAddDialogVisible = false },
                    onSave = { name, id, key ->
                        onClick(Event.AddConfig(SavedConfig(id = id, key = key, name = name)))
                        isAddDialogVisible = false
                    }
                )
            }

            if (isInstructionsDialogVisible) {
                SetupInstructionsDialog(
                    onDismiss = { isInstructionsDialogVisible = false }
                )
            }
        }

        CopyToastOverlay(
            visible = showToast,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ScriptsTabHeader(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.scripts_management),
            style = headlineMd.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
        )

        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = secondary,
                contentColor = onSecondary
            ),
            shape = roundedDefault,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.add_script_icon_desc),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(Res.string.add_script),
                style = titleSm.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun SetupInstructionsBanner(
    language: Lang,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = roundedDefault,
        colors = CardDefaults.cardColors(
            containerColor = surfaceContainerLow.copy(alpha = 0.6f)
        ),
        border = borderStrokeGlass(),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Instructions Icon",
                    tint = secondary,
                    modifier = Modifier.size(24.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(Res.string.setup_instructions_title),
                        style = titleSm.copy(fontWeight = FontWeight.Bold, color = secondary)
                    )
                    Text(
                        text = stringResource(Res.string.setup_instructions_click_to_view),
                        style = bodySm.copy(color = onSurfaceVariant, fontSize = 12.sp)
                    )
                }
            }
            Icon(
                imageVector = if (language == Lang.FA) Icons.Default.ChevronLeft else Icons.Default.ChevronRight,
                contentDescription = "Chevron Right",
                tint = onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ScriptsList(
    state: HomeState,
    onClick: (Event) -> Unit,
    onCopied: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.savedConfigs.isNotEmpty()) {
            state.savedConfigs.forEachIndexed { index, config ->
                val isActive = index == state.selectedConfigIndex
                ScriptRow(
                    name = config.name,
                    id = config.id,
                    isActive = isActive,
                    isMock = false,
                    onSelect = {
                        if (!state.isVpnRunning) {
                            onClick(Event.SelectConfig(index))
                        }
                    },
                    onDelete = {
                        if (!state.isVpnRunning) {
                            onClick(Event.DeleteConfig(config))
                        }
                    },
                    onCopied = onCopied
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CopyToastOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        modifier = modifier.padding(bottom = 32.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = surfaceContainerHighest.copy(alpha = 0.85f)),
            shape = roundedDefault,
            border = borderStrokeGlass(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = secondary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(Res.string.copied_toast),
                    style = bodySm.copy(fontWeight = FontWeight.SemiBold, color = onSurface)
                )
            }
        }
    }
}
