package com.darius.relay_vpn

import java.lang.ProcessBuilder
import kotlin.concurrent.thread
import java.io.File

object ProcessRunner {

    private val binaryPath = getPythonExecutablePath()

    private var process: Process? = null

    fun installCert() {
        println("Installing Certificate for: $binaryPath")
        val processBuilder = ProcessBuilder( "pkexec", binaryPath, "--install-cert")

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
        val processBuilder = ProcessBuilder( binaryPath)

        process = processBuilder.runProcess {
            println("[VPN Process] Process stopped!")
        }
    }

    fun stop() {
        process?.destroy()
        process = null
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
                            println("[VPN Process] $line")
                        }
                    }
                } catch (e: Exception) {
                    println("[VPN Process] Logger thread error: ${e.message}")
                }
            }
            process
        } catch (e: Exception) {
            println("[VPN Process] Failed to start process: ${e.message}")
            null
        }
    }
}
