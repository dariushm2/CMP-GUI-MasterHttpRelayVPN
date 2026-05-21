package com.darius.lionvpn.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darius.lionvpn.errorLog
import com.darius.lionvpn.ui.navigation.Screen
import kotlinx.coroutines.CoroutineExceptionHandler
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
