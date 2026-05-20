package com.darius.relay_vpn

import java.io.File

fun getPythonExecutablePath(): String {
    var resourcesDir = System.getProperty("compose.application.resources.dir")

    if (resourcesDir == null) {
        // Points directly to the "isolated-python" folder in your project root
        resourcesDir =
            "/home/dariush/Projects/master-http-relay-cmp/desktopApp/src/main/resources"
    }

    val pythonExec = File(resourcesDir, "/python-embed/python.exe")
    // Adjust based on OS
//    val pythonExec = if (System.getProperty("os.name").contains("Mac")) {
//        File(resourcesDir, "python-embed/bin/python3")
//    } else if (System.getProperty("os.name").contains("Linux")) {
//        File(resourcesDir, "python-embed/bin/python3")
//    } else {
//        File(resourcesDir, "python-embed/python.exe")
//    }

    return pythonExec.absolutePath
}
