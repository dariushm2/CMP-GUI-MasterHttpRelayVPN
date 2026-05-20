package com.darius.relay_vpn.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darius.relay_vpn.errorLog
import com.darius.relay_vpn.ui.model.ToolbarData
import com.darius.relay_vpn.ui.model.UiState
import com.darius.relay_vpn.ui.navigation.Screen
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class ScreenViewModel<S : Screen>(
    open val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    abstract val screen: Screen

    //protected abstract val toolbarData: MutableStateFlow<ToolbarData>
    //fun toolbarData(): StateFlow<ToolbarData> = toolbarData
    open fun postToolbarData() {}

    //protected abstract val state: MutableStateFlow<UiState>
    //fun state(): StateFlow<UiState> = state

    open fun fetch() {
        postToolbarData()
    }
    //abstract fun retry()

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            //state.emit(UiState.Error(throwable.message))
            errorLog(throwable)
        }
    }
}
