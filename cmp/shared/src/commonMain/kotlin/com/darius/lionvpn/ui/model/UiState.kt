package com.darius.lionvpn.ui.model

sealed class UiState {
    data object Loading : UiState()
    data class Success<T : UiData>(val items: List<T>) : UiState()
    data class Error(val message: String?) : UiState()
}
