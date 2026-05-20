package com.darius.relay_vpn

import java.lang.ProcessBuilder

fun runPythonScriptMain() {
    val pythonPath = getPythonExecutablePath()
    val scriptPath = "master_http_relay_vpn/main.py"
println(pythonPath)
    val processBuilder = ProcessBuilder(pythonPath, scriptPath)
    processBuilder.redirectErrorStream(true)

    val process = processBuilder.start()
    val output = process.inputStream.bufferedReader().readText()

    println("Python Output: $output")
}
