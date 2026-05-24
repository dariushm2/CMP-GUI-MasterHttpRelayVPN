package com.darius.lionvpn.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.darius.lionvpn.ui.model.SavedConfig
import com.darius.lionvpn.ui.theme.*
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptsTab(
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isAddDialogVisible by remember { mutableStateOf(false) }
    var isInstructionsDialogVisible by remember { mutableStateOf(false) }

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(Res.string.scripts_management),
                    style = headlineMd.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurface
                    )
                )
            }

            Button(
                onClick = { isAddDialogVisible = true },
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

        // Setup Instructions Banner Card (Clickable to open dialog)
        Card(
            shape = roundedDefault,
            colors = CardDefaults.cardColors(
                containerColor = surfaceContainerLow.copy(alpha = 0.6f)
            ),
            border = borderStrokeGlass(),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isInstructionsDialogVisible = true }
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
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Chevron Right",
                    tint = onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Scripts List Card
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Render actual scripts from HomeState
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
                        }
                    )
                }
            }
        }

        // Integrated Add Script Profile Dialog
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

        // Integrated Setup Instructions Dialog
        if (isInstructionsDialogVisible) {
            SetupInstructionsDialog(
                onDismiss = { isInstructionsDialogVisible = false }
            )
        }
    }
}

@Composable
private fun ScriptRow(
    name: String,
    id: String,
    isActive: Boolean,
    isMock: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isActive) {
        Color(0x1F4EDEA3) // 10% secondary emerald green opacity
    } else {
        Color(0x1F1E293B) // Standard glass-card dark blue/slate
    }

    val cardBorder = if (isActive) {
        BorderStroke(1.dp, secondary.copy(alpha = 0.6f))
    } else {
        BorderStroke(1.dp, outlineVariant.copy(alpha = 0.4f))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = roundedDefault,
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = gutter, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Radio indicator
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            if (isActive) secondary else Color.Transparent,
                            CircleShape
                        )
                        .border(
                            2.dp,
                            if (isActive) secondary else outlineVariant,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isActive) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(Res.string.selected_check_desc),
                            tint = onSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = name,
                        style = titleSm.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isActive) secondary else onSurface
                        )
                    )
                    val maskedId = if (id.length > 24) {
                        "${id.take(8)}...${id.takeLast(8)}"
                    } else {
                        id
                    }
                    Text(
                        text = stringResource(Res.string.deployment_id, maskedId),
                        style = monoCode.copy(
                            fontSize = 11.sp,
                            color = onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Active/Standby status badge
                Box(
                    modifier = Modifier
                        .background(
                            if (isActive) secondary.copy(alpha = 0.15f) else surfaceContainerHighest.copy(alpha = 0.4f),
                            roundedSm
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isActive) stringResource(Res.string.active) else stringResource(Res.string.standby),
                        style = labelCaps.copy(
                            fontSize = 10.sp,
                            color = if (isActive) secondary else onSurfaceVariant
                        )
                    )
                }

                // Delete profile button (only shown for non-mocks)
                if (!isMock) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.delete_profile_icon_desc),
                            tint = error.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddScriptDialog(
    isVpnRunning: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .width(480.dp)
                .padding(16.dp),
            shape = roundedLg,
            colors = CardDefaults.cardColors(
                containerColor = surfaceContainerHigh
            ),
            border = borderStrokeGlass()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.new_script_profile),
                        style = titleSm.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = secondary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.close_add_profile_icon_desc),
                            tint = onSurfaceVariant
                        )
                    }
                }

                Divider(color = outlineVariant)

                val errorProfileLabelBlank = stringResource(Res.string.error_profile_label_blank)
                val errorDeploymentIdBlank = stringResource(Res.string.error_deployment_id_blank)
                val errorAuthKeyBlank = stringResource(Res.string.error_auth_key_blank)

                // Input Name
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(Res.string.profile_label),
                        style = labelCaps.copy(color = onSurfaceVariant)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; formError = "" },
                        placeholder = { Text(stringResource(Res.string.profile_label_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = roundedDefault,
                        enabled = !isVpnRunning,
                        colors = customTextFieldColors(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Next) }
                        )
                    )
                }

                // Input ID
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(Res.string.deployment_id_label),
                        style = labelCaps.copy(color = onSurfaceVariant)
                    )
                    OutlinedTextField(
                        value = id,
                        onValueChange = { id = it; formError = "" },
                        placeholder = { Text(stringResource(Res.string.deployment_id_placeholder)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(Res.string.deployment_id_icon_desc),
                                tint = onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = roundedDefault,
                        enabled = !isVpnRunning,
                        colors = customTextFieldColors(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Next) }
                        )
                    )
                }

                // Input Auth Key
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(Res.string.relay_auth_key_label),
                        style = labelCaps.copy(color = onSurfaceVariant)
                    )
                    OutlinedTextField(
                        value = key,
                        onValueChange = { key = it; formError = "" },
                        placeholder = { Text(stringResource(Res.string.relay_auth_key_placeholder)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = stringResource(Res.string.auth_key_icon_desc),
                                tint = onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) stringResource(Res.string.hide) else stringResource(Res.string.show),
                                    style = bodySm.copy(fontWeight = FontWeight.Bold, color = primary)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = roundedDefault,
                        enabled = !isVpnRunning,
                        colors = customTextFieldColors(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                }

                if (formError.isNotEmpty()) {
                    Text(
                        text = formError,
                        color = error,
                        style = bodySm,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Save button
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            formError = errorProfileLabelBlank
                        } else if (id.isBlank()) {
                            formError = errorDeploymentIdBlank
                        } else if (key.isBlank()) {
                            formError = errorAuthKeyBlank
                        } else {
                            onSave(name.trim(), id.trim(), key.trim())
                            name = ""
                            id = ""
                            key = ""
                            formError = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = secondary,
                        contentColor = onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = roundedDefault,
                    enabled = !isVpnRunning
                ) {
                    Text(
                        text = stringResource(Res.string.save_profile),
                        style = titleSm.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = onSurface,
    unfocusedTextColor = onSurface,
    disabledTextColor = onSurfaceVariant.copy(alpha = 0.5f),
    focusedContainerColor = surfaceContainerLowest,
    unfocusedContainerColor = surfaceContainerLowest,
    focusedBorderColor = primary,
    unfocusedBorderColor = outlineVariant.copy(alpha = 0.6f),
    focusedPlaceholderColor = onSurfaceVariant.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = onSurfaceVariant.copy(alpha = 0.5f),
    focusedLabelColor = primary,
    unfocusedLabelColor = onSurfaceVariant
)

@Composable
private fun SetupInstructionsDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .width(520.dp)
                .padding(16.dp),
            shape = roundedLg,
            colors = CardDefaults.cardColors(
                containerColor = surfaceContainerHigh
            ),
            border = borderStrokeGlass()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
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
                            imageVector = Icons.Default.Info,
                            contentDescription = "Instructions Icon",
                            tint = secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(Res.string.setup_instructions_title),
                            style = titleSm.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = secondary
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = onSurfaceVariant
                        )
                    }
                }

                Divider(color = outlineVariant)

                // Scrollable Body Text with Clickable Links
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                ) {
                    val scrollState = rememberScrollState()
                    val uriHandler = LocalUriHandler.current

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.setup_instructions_intro),
                            style = bodySm.copy(color = onSurface, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
                        )

                        // Step 1
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = stringResource(Res.string.setup_instructions_step1),
                                style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
                            )
                            Text(
                                text = "https://github.com/masterking32/MasterHttpRelayVPN/blob/python_testing/apps_script/Code.gs",
                                color = primary,
                                style = bodySm.copy(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Medium),
                                modifier = Modifier
                                    .clickable { uriHandler.openUri("https://github.com/masterking32/MasterHttpRelayVPN/blob/python_testing/apps_script/Code.gs") }
                                    .padding(vertical = 2.dp)
                            )
                        }

                        // Step 2
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = stringResource(Res.string.setup_instructions_step2),
                                style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
                            )
                            Text(
                                text = "https://script.google.com/",
                                color = primary,
                                style = bodySm.copy(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Medium),
                                modifier = Modifier
                                    .clickable { uriHandler.openUri("https://script.google.com") }
                                    .padding(vertical = 2.dp)
                            )
                        }

                        // Step 3
                        Text(
                            text = stringResource(Res.string.setup_instructions_step3),
                            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
                        )

                        // Step 4
                        Text(
                            text = stringResource(Res.string.setup_instructions_step4),
                            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
                        )

                        // Step 5
                        Text(
                            text = stringResource(Res.string.setup_instructions_step5),
                            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
                        )

                        // Step 6
                        Text(
                            text = stringResource(Res.string.setup_instructions_step6),
                            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
                        )

                        // Step 7
                        Text(
                            text = stringResource(Res.string.setup_instructions_step7),
                            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
                        )
                    }
                }
            }
        }
    }
}
