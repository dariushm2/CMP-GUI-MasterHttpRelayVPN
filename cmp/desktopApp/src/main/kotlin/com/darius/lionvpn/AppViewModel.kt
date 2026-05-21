package com.darius.lionvpn

import androidx.lifecycle.ViewModel
import com.darius.lionvpn.ui.home.Event

class AppViewModel : ViewModel() {

    val isVpnRunning = ProcessRunner.isVpnRunning
    val vpnLogs = ProcessRunner.vpnLogs

    fun handleEvent(event: Event) {
        when (event) {
            Event.Certificate -> ProcessRunner.installCert()
            Event.Connect -> ProcessRunner.start()
        }
    }
}