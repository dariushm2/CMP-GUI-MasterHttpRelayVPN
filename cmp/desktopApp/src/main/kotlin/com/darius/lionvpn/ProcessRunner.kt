package com.darius.lionvpn

import java.lang.ProcessBuilder
import kotlin.concurrent.thread
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okio.IOException
import kotlin.collections.plus

object ProcessRunner {

    private val platform = getPlatform()
    private val binaryPath = getPythonExecutablePath()

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

    fun start() {
        if (process != null) {
            stop()
            return
        }

        println("Launching Python VPN binary: $binaryPath")
        val processBuilder = ProcessBuilder(binaryPath)

        process = processBuilder.runProcess {
            println("[VPN Process] Process stopped!")
            _vpnLogs.value += "VPN Process stopped!"
        }
        
        if (process != null) {
            _isVpnRunning.value = true
        }
    }

    fun stop() {
        process?.destroy()
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
