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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.darius.lionvpn.ui.model.SavedConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptsTab(
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isAddDialogVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(Theme2.containerPadding),
        verticalArrangement = Arrangement.spacedBy(Theme2.gutter)
    ) {
        // Tab Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Scripts Management",
                    style = Theme2.headlineMd.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Theme2.onSurface
                    )
                )
            }

            Button(
                onClick = { isAddDialogVisible = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Theme2.secondary,
                    contentColor = Theme2.onSecondary
                ),
                shape = Theme2.roundedDefault,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Script profile Icon",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add Script",
                    style = Theme2.titleSm.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
        BorderStroke(1.dp, Theme2.secondary.copy(alpha = 0.6f))
    } else {
        BorderStroke(1.dp, Theme2.outlineVariant.copy(alpha = 0.4f))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = Theme2.roundedDefault,
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme2.gutter, vertical = 14.dp),
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
                            if (isActive) Theme2.secondary else Color.Transparent,
                            CircleShape
                        )
                        .border(
                            2.dp,
                            if (isActive) Theme2.secondary else Theme2.outlineVariant,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isActive) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected Check",
                            tint = Theme2.onSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = name,
                        style = Theme2.titleSm.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isActive) Theme2.secondary else Theme2.onSurface
                        )
                    )
                    val maskedId = if (id.length > 24) {
                        "${id.take(8)}...${id.takeLast(8)}"
                    } else {
                        id
                    }
                    Text(
                        text = "Deployment ID: $maskedId",
                        style = Theme2.monoCode.copy(
                            fontSize = 11.sp,
                            color = Theme2.onSurfaceVariant.copy(alpha = 0.7f)
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
                            if (isActive) Theme2.secondary.copy(alpha = 0.15f) else Theme2.surfaceContainerHighest.copy(alpha = 0.4f),
                            Theme2.roundedSm
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isActive) "ACTIVE" else "STANDBY",
                        style = Theme2.labelCaps.copy(
                            fontSize = 10.sp,
                            color = if (isActive) Theme2.secondary else Theme2.onSurfaceVariant
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
                            contentDescription = "Delete Profile Icon",
                            tint = Theme2.error.copy(alpha = 0.8f),
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
            shape = Theme2.roundedLg,
            colors = CardDefaults.cardColors(
                containerColor = Theme2.surfaceContainerHigh
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
                        text = "New Script Profile",
                        style = Theme2.titleSm.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Theme2.secondary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Add Profile Icon",
                            tint = Theme2.onSurfaceVariant
                        )
                    }
                }

                Divider(color = Theme2.outlineVariant)

                // Input Name
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "PROFILE LABEL",
                        style = Theme2.labelCaps.copy(color = Theme2.onSurfaceVariant)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; formError = "" },
                        placeholder = { Text("e.g. NYC Gateway, Backup Tunnel") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = Theme2.roundedDefault,
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
                        text = "APPS SCRIPT DEPLOYMENT ID",
                        style = Theme2.labelCaps.copy(color = Theme2.onSurfaceVariant)
                    )
                    OutlinedTextField(
                        value = id,
                        onValueChange = { id = it; formError = "" },
                        placeholder = { Text("Enter Google Apps Script Deployment ID") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Deployment ID Icon",
                                tint = Theme2.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = Theme2.roundedDefault,
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
                        text = "RELAY AUTH KEY",
                        style = Theme2.labelCaps.copy(color = Theme2.onSurfaceVariant)
                    )
                    OutlinedTextField(
                        value = key,
                        onValueChange = { key = it; formError = "" },
                        placeholder = { Text("Enter secure Relay Auth Key") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Auth Key Icon",
                                tint = Theme2.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "Hide" else "Show",
                                    style = Theme2.bodySm.copy(fontWeight = FontWeight.Bold, color = Theme2.primary)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = Theme2.roundedDefault,
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
                        color = Theme2.error,
                        style = Theme2.bodySm,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Save button
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            formError = "Profile label cannot be blank."
                        } else if (id.isBlank()) {
                            formError = "Deployment ID cannot be blank."
                        } else if (key.isBlank()) {
                            formError = "Auth Key cannot be blank."
                        } else {
                            onSave(name.trim(), id.trim(), key.trim())
                            name = ""
                            id = ""
                            key = ""
                            formError = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Theme2.secondary,
                        contentColor = Theme2.onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = Theme2.roundedDefault,
                    enabled = !isVpnRunning
                ) {
                    Text(
                        text = "Save Profile",
                        style = Theme2.titleSm.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Theme2.onSurface,
    unfocusedTextColor = Theme2.onSurface,
    disabledTextColor = Theme2.onSurfaceVariant.copy(alpha = 0.5f),
    focusedContainerColor = Theme2.surfaceContainerLowest,
    unfocusedContainerColor = Theme2.surfaceContainerLowest,
    focusedBorderColor = Theme2.primary,
    unfocusedBorderColor = Theme2.outlineVariant.copy(alpha = 0.6f),
    focusedPlaceholderColor = Theme2.onSurfaceVariant.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = Theme2.onSurfaceVariant.copy(alpha = 0.5f),
    focusedLabelColor = Theme2.primary,
    unfocusedLabelColor = Theme2.onSurfaceVariant
)
