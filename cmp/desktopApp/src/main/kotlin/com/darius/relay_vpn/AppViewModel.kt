package com.darius.relay_vpn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darius.relay_vpn.ui.home.Event
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    fun handleEvent(event: Event) {
        when (event) {
            Event.Certificate -> ProcessRunner.installCert()
            Event.Connect -> ProcessRunner.start()
        }
    }
}