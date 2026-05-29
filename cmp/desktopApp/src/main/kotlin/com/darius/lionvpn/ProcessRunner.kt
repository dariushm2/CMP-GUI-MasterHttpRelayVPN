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
import com.darius.lionvpn.config.*
import com.darius.lionvpn.ui.home.ConnectionState
import kotlinx.serialization.json.*

object ProcessRunner {

    private val platform = JvmPlatform()
    private val binaryPath = getPythonExecutablePath()
    private val proxyManager: ProxyManager by lazy { getKoin().get<ProxyManager>() }

    private var process: Process? = null

    private val _isVpnRunning = MutableStateFlow(false)
    val isVpnRunning: StateFlow<Boolean> = _isVpnRunning.asStateFlow()
    private val _vpnState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val vpnState: StateFlow<ConnectionState> = _vpnState.asStateFlow()
    private val _vpnLogs = MutableStateFlow(emptyList<String>())
    val vpnLogs: StateFlow<List<String>> = _vpnLogs.asStateFlow()

    fun clearLogs() {
        _vpnLogs.value = emptyList()
    }

    fun installCert() {
        println("Installing Certificate for: $binaryPath")

        val configFile = File(getUserDataDirectory(), Constants.Config.FILE_NAME)
        val processBuilder = when(platform.os) {
            JvmPlatform.OS.WIN,
            JvmPlatform.OS.MAC -> ProcessBuilder(binaryPath, "--config", configFile.absolutePath, "--install-cert")
            else -> ProcessBuilder("pkexec", binaryPath, "--config", configFile.absolutePath, "--install-cert")
        }

        processBuilder.runProcess { isSuccess ->
            if (isSuccess) println("[VPN Process] Certificate was installed successfully!")
            else println("[VPN Process] Something went wrong!")
        }
    }

    fun uninstallCert() {
        println("Uninstalling Certificate for: $binaryPath")

        val configFile = File(getUserDataDirectory(), Constants.Config.FILE_NAME)
        val processBuilder = when(platform.os) {
            JvmPlatform.OS.WIN,
            JvmPlatform.OS.MAC -> ProcessBuilder(binaryPath, "--config", configFile.absolutePath, "--uninstall-cert")
            else -> ProcessBuilder("pkexec", binaryPath, "--config", configFile.absolutePath, "--uninstall-cert")
        }

        processBuilder.runProcess { isSuccess ->
            if (isSuccess) println("[VPN Process] Certificate was uninstalled successfully!")
            else println("[VPN Process] Something went wrong!")
        }
    }

    fun start(isSystemProxyEnabled: Boolean = true) {
        if (process != null) {
            stop()
            return
        }

        println("Launching Python VPN binary: $binaryPath")
        
        // Log starting message instantly in English only matching log timestamp pattern
        _vpnLogs.value += VpnLogger.formatInfo("VPN process is starting... warming up")

        _vpnState.value = ConnectionState.CONNECTING

        val configFile = File(getUserDataDirectory(), Constants.Config.FILE_NAME)
        val processBuilder = ProcessBuilder(binaryPath, "--config", configFile.absolutePath)

        process = processBuilder.runProcess(
            isVpnLogger = true,
            isSystemProxyEnabled = isSystemProxyEnabled,
            onExit = {
                println("[VPN Process] Process stopped!")
                _vpnLogs.value += VpnLogger.formatInfo("VPN process stopped")
                _vpnState.value = ConnectionState.DISCONNECTED
                _isVpnRunning.value = false
                if (isSystemProxyEnabled) {
                    proxyManager.disableProxy()
                }
            }
        )
        
        if (process != null) {
            _isVpnRunning.value = true
        } else {
            _vpnState.value = ConnectionState.DISCONNECTED
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
        _vpnState.value = ConnectionState.DISCONNECTED
    }

    private fun ProcessBuilder.runProcess(
        isVpnLogger: Boolean = false,
        isSystemProxyEnabled: Boolean = true,
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
                            
                            if (isVpnLogger && _vpnState.value == ConnectionState.CONNECTING) {
                                if (VpnLogger.isConnectionSuccessLog(line)) {
                                    _vpnState.value = ConnectionState.CONNECTED
                                    
                                    if (isSystemProxyEnabled) {
                                        // Parse configured dynamic HTTP port and host from config.json
                                        val rawConfig = loadRawConfig()
                                        val (host, port) = try {
                                            val configObj = Json.parseToJsonElement(rawConfig).jsonObject
                                            val h = configObj["listen_host"]?.jsonPrimitive?.content ?: "127.0.0.1"
                                            val p = configObj["http_port"]?.jsonPrimitive?.intOrNull ?: 8085
                                            h to p
                                        } catch (e: Exception) {
                                            "127.0.0.1" to 8085
                                        }
                                        println("[VPN Process] Enabling system proxy forwarding on dynamic address: $host:$port")
                                        proxyManager.enableProxy(host, port)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: IOException) {
                    _vpnLogs.value += VpnLogger.formatInfo("Logger thread error: ${e.message}")
                    println("[VPN Process] Logger thread error: ${e.message}")
                }
            }
            process
        } catch (e: IOException) {
            _vpnLogs.value += VpnLogger.formatInfo("Failed to start process: ${e.message}")
            println("[VPN Process] Failed to start process: ${e.message}")
            _isVpnRunning.value = false
            _vpnState.value = ConnectionState.DISCONNECTED
            null
        }
    }
}
