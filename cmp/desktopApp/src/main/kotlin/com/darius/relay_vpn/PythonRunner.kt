package com.darius.relay_vpn

import java.lang.ProcessBuilder
import kotlin.concurrent.thread
import java.io.File

fun runPythonScriptMain() {
    val binaryPath = getPythonExecutablePath()
    println("Launching Python VPN binary: $binaryPath")
    
    val processBuilder = ProcessBuilder(binaryPath)
    
    // Set working directory to the directory containing the binary
    // to ensure relative files (like certificates, logs, configs) are resolved correctly
    val binaryFile = File(binaryPath)
    if (binaryFile.parentFile != null) {
        processBuilder.directory(binaryFile.parentFile)
    }
    
    processBuilder.redirectErrorStream(true)

    try {
        val process = processBuilder.start()
        
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
    } catch (e: Exception) {
        println("[VPN Process] Failed to start process: ${e.message}")
    }
}

