package com.darius.lionvpn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.home.ConnectionState
import com.darius.lionvpn.config.*
import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.model.SavedConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel : ViewModel() {

    private val _savedConfigs = MutableStateFlow<List<SavedConfig>>(emptyList())
    private val _selectedConfigIndex = MutableStateFlow(-1)
    private val _rawConfigJson = MutableStateFlow("")
    private val _configResetTrigger = MutableStateFlow(0)
    private val _language = MutableStateFlow(loadLanguagePreference())

    private val vpnState = ProcessRunner.vpnState
    private val vpnLogs = ProcessRunner.vpnLogs

    // Reactively compile dynamic HomeState from underlying states using stateIn
    val homeState: StateFlow<HomeState> = combine(
        vpnState,
        vpnLogs,
        _savedConfigs,
        _selectedConfigIndex,
        _rawConfigJson,
        _configResetTrigger,
        _language
    ) { array ->
        val state = array[0] as ConnectionState
        val logs = array[1] as List<String>
        val configs = array[2] as List<SavedConfig>
        val index = array[3] as Int
        val configJson = array[4] as String
        val resetTrigger = array[5] as Int
        val lang = array[6] as Lang
        
        HomeState(
            connectionState = state,
            log = logs,
            savedConfigs = configs,
            selectedConfigIndex = index,
            rawConfigJson = configJson,
            configResetTrigger = resetTrigger,
            language = lang
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState(language = loadLanguagePreference())
    )

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        println("[AppViewModel Error] Exception caught in Coroutine: ${exception.localizedMessage}")
    }

    init {
        // Synchronously load and set default JVM locale on startup
        val savedLang = loadLanguagePreference()
        _language.value = savedLang
        java.util.Locale.setDefault(java.util.Locale(savedLang.name.lowercase()))

        viewModelScope.launch(errorHandler) {
            loadConfigs()
        }
    }

    private suspend fun loadConfigs() = withContext(Dispatchers.IO) {
        val configs = loadSavedScripts()
        val index = loadActiveScriptIndex()
        _savedConfigs.value = configs
        _rawConfigJson.value = loadRawConfig()
        _configResetTrigger.value++
        
        // Ensure index is valid, otherwise default to first configuration if list is not empty
        if (configs.isNotEmpty()) {
            if (index < 0 || index >= configs.size) {
                _selectedConfigIndex.value = 0
                saveActiveScriptIndex(0)
                val active = configs[0]
                saveConfigLocally(active.id, active.key)
                _rawConfigJson.value = loadRawConfig()
                _configResetTrigger.value++
            } else {
                _selectedConfigIndex.value = index
                val active = configs[index]
                saveConfigLocally(active.id, active.key)
                _rawConfigJson.value = loadRawConfig()
                _configResetTrigger.value++
            }
        } else {
            _selectedConfigIndex.value = -1
            saveActiveScriptIndex(-1)
        }
    }

    private suspend fun addConfig(config: SavedConfig) = withContext(Dispatchers.IO) {
        val currentList = _savedConfigs.value.toMutableList()
        // Prevent duplicate IDs if they try to save again
        if (currentList.none { it.id == config.id }) {
            currentList.add(config)
            saveSavedScripts(currentList)
            _savedConfigs.value = currentList
            
            // If this was the first script added, automatically select it
            if (currentList.size == 1) {
                selectConfig(0)
            } else {
                loadConfigs()
            }
        }
    }

    private suspend fun deleteConfig(config: SavedConfig) = withContext(Dispatchers.IO) {
        val currentList = _savedConfigs.value.toMutableList()
        val indexToDelete = currentList.indexOf(config)
        if (indexToDelete == -1) return@withContext
        
        currentList.removeAt(indexToDelete)
        saveSavedScripts(currentList)
        _savedConfigs.value = currentList
        
        val currentIndex = _selectedConfigIndex.value
        val newIndex = when {
            currentList.isEmpty() -> -1
            currentIndex == indexToDelete -> 0 // Selected profile got deleted, default to first available
            currentIndex > indexToDelete -> currentIndex - 1 // Shift index left
            else -> currentIndex
        }
        
        saveActiveScriptIndex(newIndex)
        _selectedConfigIndex.value = newIndex
        
        if (newIndex in currentList.indices) {
            val active = currentList[newIndex]
            saveConfigLocally(active.id, active.key)
            _rawConfigJson.value = loadRawConfig()
            _configResetTrigger.value++
        } else {
            // Write clean blank values to config.json
            saveConfigLocally("", "")
            _rawConfigJson.value = loadRawConfig()
            _configResetTrigger.value++
        }
    }

    private suspend fun selectConfig(index: Int) = withContext(Dispatchers.IO) {
        val configs = _savedConfigs.value
        if (index in configs.indices) {
            saveActiveScriptIndex(index)
            _selectedConfigIndex.value = index
            val active = configs[index]
            saveConfigLocally(active.id, active.key)
            _rawConfigJson.value = loadRawConfig()
            _configResetTrigger.value++
        }
    }

    fun handleEvent(event: Event) {
        viewModelScope.launch(errorHandler) {
            when (event) {
                Event.InstallCertificate -> {
                    withContext(Dispatchers.IO) {
                        ProcessRunner.installCert()
                    }
                }
                Event.UninstallCertificate -> {
                    withContext(Dispatchers.IO) {
                        ProcessRunner.uninstallCert()
                    }
                }
                Event.Connect -> {
                    withContext(Dispatchers.IO) {
                        ProcessRunner.start()
                    }
                }
                Event.ClearLogs -> ProcessRunner.clearLogs()
                is Event.AddConfig -> addConfig(event.config)
                is Event.DeleteConfig -> deleteConfig(event.config)
                is Event.SelectConfig -> selectConfig(event.index)
                is Event.SaveRawConfig -> {
                    withContext(Dispatchers.IO) {
                        saveRawConfig(event.json)
                        _rawConfigJson.value = loadRawConfig()
                    }
                }
                Event.LoadDefaultConfig -> {
                    withContext(Dispatchers.IO) {
                        val defaultContent = loadDefaultConfigContent()
                        saveRawConfig(defaultContent)
                        _rawConfigJson.value = loadRawConfig()
                        _configResetTrigger.value++
                    }
                }
                is Event.ChangeLanguage -> {
                    val lang = event.language
                    java.util.Locale.setDefault(java.util.Locale(lang.name))
                    withContext(Dispatchers.IO) {
                        saveLanguagePreference(lang)
                    }
                    _language.value = lang
                }
            }
        }
    }
}
