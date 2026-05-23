package com.darius.lionvpn.proxy

import com.darius.lionvpn.getPlatform
import java.lang.ProcessBuilder

object LinuxProxyManager : ProxyManager {
    
    private fun runCommand(vararg command: String) {
        try {
            ProcessBuilder(*command).start().waitFor()
        } catch (e: Exception) {
            println("[Linux Proxy Manager] Error running command: ${e.message}")
        }
    }

    override fun enableProxy(port: Int) {
        if (!getPlatform().isLinux()) return

        println("[Linux Proxy Manager] Enabling Linux system proxy (GNOME) to 127.0.0.1:$port")
        runCommand("gsettings", "set", "org.gnome.system.proxy.http", "host", "127.0.0.1")
        runCommand("gsettings", "set", "org.gnome.system.proxy.http", "port", port.toString())
        runCommand("gsettings", "set", "org.gnome.system.proxy.https", "host", "127.0.0.1")
        runCommand("gsettings", "set", "org.gnome.system.proxy.https", "port", port.toString())
        runCommand("gsettings", "set", "org.gnome.system.proxy", "mode", "manual")
    }

    override fun disableProxy() {
        if (!getPlatform().isLinux()) return

        println("[Linux Proxy Manager] Disabling Linux system proxy (GNOME)")
        runCommand("gsettings", "set", "org.gnome.system.proxy", "mode", "none")
    }
}
