package com.darius.lionvpn.ui.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ToolbarData(
    val title: String,
    val actions: List<ActionButton> = emptyList(),
    val showHomeAsUp: Boolean = true,
) : UiData() {
    override val id: Int = hashCode()

    data class ActionButton(
        val icon: ImageVector,
        val action: () -> Unit,
        val destinationRoute: String? = null,
    )
}
