package com.darius.relay_vpn.ui.model

sealed class UiState {
    data object Loading : UiState()
    data class Success<T : UiData>(val items: List<T>) : UiState()
    data class Error(val message: String?) : UiState()
}
