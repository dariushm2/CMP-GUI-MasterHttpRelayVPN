package com.darius.lionvpn

import java.lang.ProcessBuilder
import kotlin.concurrent.thread
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okio.IOException
import kotlin.collections.plus

import org.koin.mp.KoinPlatform.getKoin
import com.darius.lionvpn.proxy.ProxyManager

object ProcessRunner {

    private val platform = getPlatform()
    private val binaryPath = getPythonExecutablePath()
    private val proxyManager: ProxyManager by lazy { getKoin().get<ProxyManager>() }

    private var process: Process? = null

    private val _isVpnRunning = MutableStateFlow(false)
    val isVpnRunning: StateFlow<Boolean> = _isVpnRunning.asStateFlow()
    private val _vpnLogs = MutableStateFlow(listOf("Lion VPN"))
    val vpnLogs: StateFlow<List<String>> = _vpnLogs.asStateFlow()

    fun installCert() {
        println("Installing Certificate for: $binaryPath")

        val processBuilder = when {
            platform.isWin() -> ProcessBuilder(binaryPath, "--install-cert")
            platform.isMac() -> ProcessBuilder(binaryPath, "--install-cert")
            else -> ProcessBuilder("pkexec", binaryPath, "--install-cert")
        }

        processBuilder.runProcess { isSuccess ->
            if (isSuccess) println("[VPN Process] Certificate was installed successfully!")
            else println("[VPN Process] Something went wrong!")
        }
    }

    fun start(isSystemProxyEnabled: Boolean = true) {
        if (process != null) {
            stop()
            return
        }

        println("Launching Python VPN binary: $binaryPath")
        val processBuilder = ProcessBuilder(binaryPath)

        process = processBuilder.runProcess {
            println("[VPN Process] Process stopped!")
            _vpnLogs.value += "VPN Process stopped!"
            if (isSystemProxyEnabled) {
                proxyManager.disableProxy()
            }
        }
        
        if (process != null) {
            _isVpnRunning.value = true
            if (isSystemProxyEnabled) {
                proxyManager.enableProxy(8085)
            }
        }
    }

    fun stop() {
        try {
            proxyManager.disableProxy()
        } catch (e: Exception) {
            println("[VPN Process] Failed to disable proxy: ${e.message}")
        }

        process?.let { p ->
            try {
                // Kill all descendant processes first. This is crucial on Windows because PyInstaller's
                // --onefile executable spawns the actual python process as a child, and standard JVM p.destroy()
                // only terminates the bootloader process, leaving the child process orphaned and running.
                p.descendants().forEach { descendant ->
                    descendant.destroyForcibly()
                }
            } catch (e: Exception) {
                println("[VPN Process] Failed to destroy descendants: ${e.message}")
            }
            p.destroyForcibly()
        }
        process = null
        _isVpnRunning.value = false
    }

    private fun ProcessBuilder.runProcess(
        onExit: (Boolean) -> Unit = {},
    ): Process? {
        // Set working directory to the directory containing the binary
        // to ensure relative files (like certificates, logs, configs) are resolved correctly
        val binaryFile = File(binaryPath)
        if (binaryFile.parentFile != null) {
            this.directory(binaryFile.parentFile)
        }

        this.redirectErrorStream(true)

        return try {
            val process = this.start()
            process.onExit().thenAccept { finishedProcess ->
                onExit(finishedProcess.exitValue() == 0)
            }
            // Read output asynchronously to prevent blocking Compose UI thread
            thread(isDaemon = true) {
                try {
                    process.inputStream.bufferedReader().useLines { lines ->
                        lines.forEach { line ->
                            _vpnLogs.value += line
                            println("[VPN Process] $line")
                        }
                    }
                } catch (e: IOException) {
                    _vpnLogs.value += "Logger thread error: ${e.message}"
                    println("[VPN Process] Logger thread error: ${e.message}")
                }
            }
            process
        } catch (e: IOException) {
            _vpnLogs.value += "Failed to start process: ${e.message}"
            println("[VPN Process] Failed to start process: ${e.message}")
            _isVpnRunning.value = false
            null
        }
    }
}
