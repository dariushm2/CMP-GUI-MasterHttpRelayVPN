package com.darius.lionvpn.proxy

import com.darius.lionvpn.getPlatform
import java.lang.ProcessBuilder

object WindowsProxyManager : ProxyManager {

    private fun runCommand(vararg command: String) {
        try {
            ProcessBuilder(*command).start().waitFor()
        } catch (e: Exception) {
            println("[Proxy Manager] Error running command: ${e.message}")
        }
    }

    private fun refreshSystemSettings() {
        val refreshCmd = "[void](Add-Type -MemberDefinition '[DllImport(\"wininet.dll\")] public static extern bool InternetSetOption(IntPtr hInternet, int dwOption, IntPtr lpBuffer, int dwBufferLength);' -Name WinInet -Namespace Win32 -PassThru)::InternetSetOption([IntPtr]::Zero, 39, [IntPtr]::Zero, 0); [void]([Win32.WinInet]::InternetSetOption([IntPtr]::Zero, 37, [IntPtr]::Zero, 0))"
        runCommand("powershell", "-NoProfile", "-Command", refreshCmd)
    }

    override fun enableProxy(port: Int) {
        if (!getPlatform().isWin()) return

        println("[Proxy Manager] Enabling Windows system proxy to 127.0.0.1:$port")
        runCommand("reg", "add", "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "/v", "ProxyEnable", "/t", "REG_DWORD", "/d", "1", "/f")
        runCommand("reg", "add", "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "/v", "ProxyServer", "/t", "REG_SZ", "/d", "127.0.0.1:$port", "/f")
        runCommand("reg", "add", "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "/v", "ProxyOverride", "/t", "REG_SZ", "/d", "<local>;localhost;127.0.0.1", "/f")
        refreshSystemSettings()
    }

    override fun disableProxy() {
        if (!getPlatform().isWin()) return

        println("[Proxy Manager] Disabling Windows system proxy")
        runCommand("reg", "add", "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "/v", "ProxyEnable", "/t", "REG_DWORD", "/d", "0", "/f")
        refreshSystemSettings()
    }
}
