package com.darius.lionvpn

object VpnLogger {

    fun formatInfo(tag: String, message: String): String {
        return "${getCurrentTimeString()}  • INFO   [${tag.padEnd(8)}]  $message"
    }

    fun isConnectionSuccessLog(line: String): Boolean {
        return line.contains("HTTP proxy listening on") || line.contains("SOCKS5 proxy listening on")
    }
}
