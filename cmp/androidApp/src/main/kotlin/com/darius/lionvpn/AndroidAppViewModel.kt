package com.darius.lionvpn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darius.lionvpn.model.AndroidUiEffect
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.model.Lang
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
import kotlinx.serialization.json.jsonObject

class AndroidAppViewModel : ViewModel() {

    private val _savedConfigs = MutableStateFlow<List<SavedConfig>>(emptyList())
    val savedConfigs: StateFlow<List<SavedConfig>> = _savedConfigs.asStateFlow()

    private val _selectedConfigIndex = MutableStateFlow(-1)
    val selectedConfigIndex: StateFlow<Int> = _selectedConfigIndex.asStateFlow()

    val isVpnRunning = ProxyService.isVpnRunning
    val vpnLogs = ProxyService.vpnLogs

    private val _rawConfigJson = MutableStateFlow("")
    val rawConfigJson: StateFlow<String> = _rawConfigJson.asStateFlow()

    private val _configResetTrigger = MutableStateFlow(0)

    private val _language = MutableStateFlow(Lang.FA)
    val language: StateFlow<Lang> = _language.asStateFlow()

    private val _showInstructionsDialog = MutableStateFlow(false)
    val showInstructionsDialog: StateFlow<Boolean> = _showInstructionsDialog.asStateFlow()

    // Expose dynamic HomeState compiled reactively from underlying flows using stateIn
    val homeState: StateFlow<HomeState> = combine(
        isVpnRunning,
        vpnLogs,
        _savedConfigs,
        _selectedConfigIndex,
        _rawConfigJson,
        _configResetTrigger,
        _language
    ) { array ->
        val running = array[0] as Boolean
        val logs = array[1] as List<String>
        val configs = array[2] as List<SavedConfig>
        val index = array[3] as Int
        val configJson = array[4] as String
        val resetTrigger = array[5] as Int
        val lang = array[6] as Lang

        HomeState(
            isVpnRunning = running,
            log = logs,
            savedConfigs = configs,
            selectedConfigIndex = index,
            rawConfigJson = configJson,
            configResetTrigger = resetTrigger,
            language = lang,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState()
    )

    private val _uiEffect = MutableSharedFlow<AndroidUiEffect>()
    val uiEffect: SharedFlow<AndroidUiEffect> = _uiEffect.asSharedFlow()

    fun initializeConfigs(
        configs: List<SavedConfig>,
        selectedIndex: Int,
        rawConfig: String,
        lang: Lang
    ) {
        _savedConfigs.value = configs
        _selectedConfigIndex.value = selectedIndex
        _rawConfigJson.value = rawConfig
        _language.value = lang
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
                is Event.SaveRawConfig -> {
                    _rawConfigJson.value = event.json
                }
                Event.LoadDefaultConfig -> {
                    throw IllegalStateException("LoadDefaultConfig should be handled by the UI/Activity layer")
                }
                is Event.ChangeLanguage -> {
                    _language.value = event.language
                }
            }
        }
    }

    fun onLoadDefaultConfig(defaultJson: String) {
        _rawConfigJson.value = defaultJson
        _configResetTrigger.value++
    }

    private fun updateRawConfigWithActiveProfile(id: String, key: String) {
        val currentJson = _rawConfigJson.value
        val updatedJson = try {
            if (currentJson.isNotBlank()) {
                val jsonMap = kotlinx.serialization.json.Json.parseToJsonElement(currentJson).jsonObject.toMutableMap()
                jsonMap["script_id"] = kotlinx.serialization.json.JsonPrimitive(id)
                jsonMap["auth_key"] = kotlinx.serialization.json.JsonPrimitive(key)
                val prettyJson = kotlinx.serialization.json.Json { prettyPrint = true }
                prettyJson.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), kotlinx.serialization.json.JsonObject(jsonMap))
            } else {
                ""
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse/update raw config json", e)
        }
        _rawConfigJson.value = updatedJson
        _configResetTrigger.value++
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
        if (nextIndex in newList.indices) {
            val active = newList[nextIndex]
            updateRawConfigWithActiveProfile(active.id, active.key)
        }
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
        if (nextIndex in newList.indices) {
            val active = newList[nextIndex]
            updateRawConfigWithActiveProfile(active.id, active.key)
        } else {
            updateRawConfigWithActiveProfile("", "")
        }
    }

    private fun selectConfig(index: Int) {
        if (index in _savedConfigs.value.indices) {
            _selectedConfigIndex.value = index
            val active = _savedConfigs.value[index]
            updateRawConfigWithActiveProfile(active.id, active.key)
        }
    }
}
