package com.darius.lionvpn.proxy

import com.darius.lionvpn.getPlatform
import java.io.BufferedReader
import java.io.InputStreamReader

object MacosProxyManager : ProxyManager {
    
    private fun runCommand(vararg command: String): String {
        return try {
            val process = ProcessBuilder(*command).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readText().trim()
            process.waitFor()
            output
        } catch (e: Exception) {
            println("[Mac Proxy Manager] Error running command ${command.joinToString(" ")}: ${e.message}")
            ""
        }
    }

    private fun getNetworkServices(): List<String> {
        val output = runCommand("networksetup", "-listallnetworkservices")
        return output.lines().filter { it.isNotBlank() && !it.startsWith("An asterisk") }
    }

    override fun enableProxy(port: Int) {
        if (!getPlatform().isMac()) return

        println("[Mac Proxy Manager] Enabling macOS system proxy to 127.0.0.1:$port")
        val services = getNetworkServices()
        for (service in services) {
            runCommand("networksetup", "-setwebproxy", service, "127.0.0.1", port.toString())
            runCommand("networksetup", "-setsecurewebproxy", service, "127.0.0.1", port.toString())
        }
    }

    override fun disableProxy() {
        if (!getPlatform().isMac()) return

        println("[Mac Proxy Manager] Disabling macOS system proxy")
        val services = getNetworkServices()
        for (service in services) {
            runCommand("networksetup", "-setwebproxystate", service, "off")
            runCommand("networksetup", "-setsecurewebproxystate", service, "off")
        }
    }
}
