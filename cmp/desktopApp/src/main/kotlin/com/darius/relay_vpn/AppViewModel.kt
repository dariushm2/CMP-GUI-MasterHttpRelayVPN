package com.darius.relay_vpn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    fun runMain() {
        println("run main")
        //viewModelScope.runCatching {
            runPythonScriptMain()
        //}
    }
}