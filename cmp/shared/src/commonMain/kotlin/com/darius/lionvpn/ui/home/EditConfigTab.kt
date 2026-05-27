package com.darius.lionvpn.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.darius.lionvpn.ui.theme.*
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditConfigTab(
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local state to keep track of the text edits in the workspace
    var jsonInput by remember(state.configResetTrigger) { mutableStateOf(state.rawConfigJson) }
    
    // Evaluation error message
    var syntaxError by remember { mutableStateOf<String?>(null) }
    
    // Success Toast notification state
    var showSuccessToast by remember { mutableStateOf(false) }

    // Focus Requester to auto-focus the editor when tab is loaded
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        // Request focus to show the keyboard/cursor automatically
        focusRequester.requestFocus()
    }

    LaunchedEffect(showSuccessToast) {
        if (showSuccessToast) {
            delay(2000)
            showSuccessToast = false
        }
    }

    val errorInvalidJson = stringResource(Res.string.error_invalid_json)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(containerPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(gutter)
        ) {
            // Premium Code Workspace Card styled like a dark log terminal console
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = roundedDefault,
                colors = CardDefaults.cardColors(containerColor = surfaceContainerLowest),
                border = borderStrokeGlass()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header bar matching the Dashboard TerminalLog header style
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
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(Res.string.tab_edit_config),
                                tint = primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "config.json",
                                style = labelCaps.copy(color = onSurfaceVariant)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Reload/Load Defaults button
                            IconButton(
                                onClick = {
                                    syntaxError = null // Clear any errors
                                    onClick(Event.LoadDefaultConfig)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SettingsBackupRestore,
                                    contentDescription = stringResource(Res.string.load_defaults),
                                    tint = onSurfaceVariant.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Save Settings button
                            IconButton(
                                onClick = {
                                    try {
                                        // Evaluation step: parse JSON directly using kotlinx.serialization
                                        Json.parseToJsonElement(jsonInput)
                                        
                                        // If it parses successfully, save it!
                                        syntaxError = null
                                        onClick(Event.SaveRawConfig(jsonInput))
                                        showSuccessToast = true
                                    } catch (e: Exception) {
                                        // Catch parsing syntax exceptions and display inline error banner
                                        syntaxError = e.localizedMessage ?: e.message ?: "Unknown syntax error"
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = stringResource(Res.string.save_settings),
                                    tint = secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Content - Code Workspace
                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = { 
                            jsonInput = it
                            syntaxError = null // Clear error when user changes text
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)) // Terminal-style deep console background
                            .focusRequester(focusRequester) // Auto-focus requested
                            .padding(4.dp),
                        textStyle = monoCode.copy(
                            fontSize = 12.sp,
                            color = onSurface,
                            textDirection = TextDirection.Ltr // JSON keys/values are English LTR
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

            // Dismissible Syntax Evaluation Error Banner
            if (syntaxError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                    text = errorInvalidJson,
                                    style = bodySm.copy(fontWeight = FontWeight.Bold, color = error)
                                )
                                Text(
                                    text = stringResource(Res.string.json_syntax_error, syntaxError ?: ""),
                                    style = monoCode.copy(fontSize = 11.sp, color = onSurfaceVariant)
                                )
                            }
                        }
                        
                        // Close button to reclaim code editing space
                        IconButton(
                            onClick = { syntaxError = null },
                            modifier = Modifier
                                .size(24.dp)
                                .padding(start = 4.dp)
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
        }

        // Custom Success Toast Overlay
        androidx.compose.animation.AnimatedVisibility(
            visible = showSuccessToast,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { it / 2 }),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
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
}
