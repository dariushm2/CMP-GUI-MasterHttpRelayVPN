package com.darius.lionvpn

object VpnLogger {

    fun formatInfo(message: String): String {
        return "${getCurrentTimeString()}  • INFO   [Client  ]  $message"
    }

    fun isConnectionSuccessLog(line: String): Boolean {
        return line.contains("HTTP proxy listening on") || line.contains("SOCKS5 proxy listening on")
    }

    /**
     * Parses the count of Google Apps Script executions used so far from the logs.
     * Searches the logs from newest to oldest.
     */
    fun parseAppsScriptExecutionCount(log: List<String>): Int {
        return try {
            log.asReversed().firstOrNull {
                it.contains("Apps Script executions used so far:", ignoreCase = true)
            }?.substringAfter("Apps Script executions used so far:")
                ?.trim()
                ?.substringBefore(" ")
                ?.trim()
                ?.toIntOrNull() ?: 0
        } catch (e: Exception) {
            0
        }
    }
}

/**
 * Appends a new line to the log list and caps the size at [capLimit] to prevent memory leaks.
 */
fun List<String>.appendCappedLog(newLine: String, capLimit: Int = 300): List<String> {
    val list = this.toMutableList()
    if (list.size >= capLimit) {
        list.removeAt(0)
    }
    list.add(newLine)
    return list
}
