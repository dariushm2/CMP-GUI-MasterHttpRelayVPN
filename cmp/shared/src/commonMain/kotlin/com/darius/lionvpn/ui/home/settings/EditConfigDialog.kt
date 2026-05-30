package com.darius.lionvpn.ui.home.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.darius.lionvpn.Constants
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.error
import com.darius.lionvpn.ui.theme.errorContainer
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.labelCaps
import com.darius.lionvpn.ui.theme.monoCode
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.outlineVariant
import com.darius.lionvpn.ui.theme.roundedDefault
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.surfaceContainerLow
import com.darius.lionvpn.ui.theme.surfaceContainerHighest
import com.darius.lionvpn.ui.theme.surfaceContainerLowest
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.close_editor
import lion_vpn.shared.generated.resources.load_defaults
import lion_vpn.shared.generated.resources.save_settings
import lion_vpn.shared.generated.resources.error_invalid_json
import lion_vpn.shared.generated.resources.json_syntax_error
import lion_vpn.shared.generated.resources.settings_saved_success
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditConfigDialog(
    state: HomeState,
    onClick: (Event) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = roundedLg,
            color = surfaceContainerLowest,
            border = borderStrokeGlass()
        ) {
            ConfigDialogContent(
                state = state,
                onClick = onClick,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun ConfigDialogContent(
    state: HomeState,
    onClick: (Event) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var jsonInput by remember(state.configResetTrigger) { mutableStateOf(state.rawConfigJson) }
    var syntaxError by remember { mutableStateOf<String?>(null) }
    var showSuccessToast by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(showSuccessToast) {
        if (showSuccessToast) {
            delay(2000)
            showSuccessToast = false
        }
    }

    Box(modifier = modifier.fillMaxSize().padding(gutter)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(gutter)
        ) {
            ConfigDialogHeader(
                onDismiss = onDismiss,
                onLoadDefaults = {
                    syntaxError = null
                    onClick(Event.LoadDefaultConfig)
                },
                onSave = {
                    try {
                        Json.parseToJsonElement(jsonInput)
                        syntaxError = null
                        onClick(Event.SaveRawConfig(jsonInput))
                        showSuccessToast = true
                    } catch (e: Exception) {
                        syntaxError = e.message ?: "Unknown syntax error"
                    }
                }
            )

            ConfigDialogEditor(
                jsonInput = jsonInput,
                onValueChange = {
                    jsonInput = it
                    syntaxError = null
                },
                focusRequester = focusRequester,
                modifier = Modifier.weight(1f)
            )

            ConfigDialogErrorBanner(
                syntaxError = syntaxError,
                onDismissError = { syntaxError = null }
            )
        }

        ConfigDialogSuccessToast(visible = showSuccessToast)
    }
}

@Composable
private fun ConfigDialogHeader(
    onDismiss: () -> Unit,
    onLoadDefaults: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceContainerLow, roundedDefault)
            .padding(horizontal = gutter, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.close_editor),
                    tint = onSurfaceVariant
                )
            }
            Text(
                text = Constants.Config.FILE_NAME,
                style = labelCaps.copy(color = onSurfaceVariant)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onLoadDefaults, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.SettingsBackupRestore,
                    contentDescription = stringResource(Res.string.load_defaults),
                    tint = onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(onClick = onSave, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = stringResource(Res.string.save_settings),
                    tint = secondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ConfigDialogEditor(
    jsonInput: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.4f), roundedDefault)
            .border(1.dp, outlineVariant.copy(alpha = 0.3f), roundedDefault)
            .padding(4.dp)
    ) {
        OutlinedTextField(
            value = jsonInput,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester),
            textStyle = monoCode.copy(
                fontSize = 12.sp,
                color = onSurface,
                textDirection = TextDirection.Ltr
            ),
            singleLine = false,
            placeholder = {
                Text(
                    text = "{ }",
                    style = monoCode.copy(color = onSurfaceVariant.copy(alpha = 0.4f))
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = onSurface,
                unfocusedTextColor = onSurface,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = secondary
            )
        )
    }
}

@Composable
private fun ConfigDialogErrorBanner(
    syntaxError: String?,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (syntaxError == null) return
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = roundedDefault,
        colors = CardDefaults.cardColors(containerColor = errorContainer.copy(alpha = 0.2f)),
        border = BorderStroke(1.dp, error.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Syntax Error",
                    tint = error,
                    modifier = Modifier.size(18.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(Res.string.error_invalid_json),
                        style = bodySm.copy(fontWeight = FontWeight.Bold, color = error)
                    )
                    Text(
                        text = stringResource(Res.string.json_syntax_error, syntaxError),
                        style = monoCode.copy(fontSize = 11.sp, color = onSurfaceVariant)
                    )
                }
            }
            
            IconButton(
                onClick = onDismissError,
                modifier = Modifier.size(24.dp).padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss Error",
                    tint = error.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ConfigDialogSuccessToast(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { it / 2 }),
        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically(targetOffsetY = { it / 2 }),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        content = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = surfaceContainerHighest.copy(alpha = 0.9f)),
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
                            text = stringResource(Res.string.settings_saved_success),
                            style = bodySm.copy(fontWeight = FontWeight.SemiBold, color = onSurface)
                        )
                    }
                }
            }
        }
    )
}
