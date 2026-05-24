package com.darius.lionvpn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.model.SavedConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AndroidUiEffect {
    object ConnectVpn : AndroidUiEffect
    object CheckAndSaveCertificate : AndroidUiEffect
}

class AndroidAppViewModel : ViewModel() {

    private val _savedConfigs = MutableStateFlow<List<SavedConfig>>(emptyList())
    val savedConfigs: StateFlow<List<SavedConfig>> = _savedConfigs.asStateFlow()

    private val _selectedConfigIndex = MutableStateFlow(-1)
    val selectedConfigIndex: StateFlow<Int> = _selectedConfigIndex.asStateFlow()

    val isVpnRunning = ProxyService.isVpnRunning
    val vpnLogs = ProxyService.vpnLogs

    private val _showInstructionsDialog = MutableStateFlow(false)
    val showInstructionsDialog: StateFlow<Boolean> = _showInstructionsDialog.asStateFlow()

    // Expose dynamic HomeState compiled reactively from underlying flows using stateIn
    val homeState: StateFlow<HomeState> = combine(
        isVpnRunning,
        vpnLogs,
        _savedConfigs,
        _selectedConfigIndex
    ) { running, logs, configs, index ->
        HomeState(
            isVpnRunning = running,
            log = logs,
            savedConfigs = configs,
            selectedConfigIndex = index
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState()
    )

    private val _uiEffect = MutableSharedFlow<AndroidUiEffect>()
    val uiEffect: SharedFlow<AndroidUiEffect> = _uiEffect.asSharedFlow()

    fun initializeConfigs(configs: List<SavedConfig>, selectedIndex: Int) {
        _savedConfigs.value = configs
        _selectedConfigIndex.value = selectedIndex
    }

    fun handleEvent(event: Event) {
        viewModelScope.launch {
            when (event) {
                is Event.Connect -> connectVpn()
                is Event.InstallCertificate -> generateAndInstallCert()
                is Event.UninstallCertificate -> { /* TODO */ }
                is Event.ClearLogs -> ProxyService.clearLogs()
                is Event.AddConfig -> addConfig(event.config)
                is Event.DeleteConfig -> deleteConfig(event.config)
                is Event.SelectConfig -> selectConfig(event.index)
            }
        }
    }

    fun onCertSavedSuccess() {
        _showInstructionsDialog.value = true
    }

    fun setInstructionsDialogVisible(visible: Boolean) {
        _showInstructionsDialog.value = visible
    }

    private suspend fun connectVpn() {
        _uiEffect.emit(AndroidUiEffect.ConnectVpn)
    }

    private suspend fun generateAndInstallCert() {
        _uiEffect.emit(AndroidUiEffect.CheckAndSaveCertificate)
    }

    private fun addConfig(config: SavedConfig) {
        val newList = _savedConfigs.value + config
        _savedConfigs.value = newList
        var nextIndex = _selectedConfigIndex.value
        if (nextIndex == -1) {
            nextIndex = 0
        }
        _selectedConfigIndex.value = nextIndex
    }

    private fun deleteConfig(config: SavedConfig) {
        val newList = _savedConfigs.value.filter { it != config }
        var nextIndex = _selectedConfigIndex.value
        if (nextIndex >= newList.size) {
            nextIndex = newList.size - 1
        }
        if (newList.isEmpty()) {
            nextIndex = -1
        }
        _savedConfigs.value = newList
        _selectedConfigIndex.value = nextIndex
    }

    private fun selectConfig(index: Int) {
        if (index in _savedConfigs.value.indices) {
            _selectedConfigIndex.value = index
        }
    }
}
