package com.darius.lionvpn

object VpnLogger {

    fun formatInfo(message: String): String {
        return "${getCurrentTimeString()}  • INFO   [Client  ]  $message"
    }

    fun isConnectionSuccessLog(line: String): Boolean {
        return line.contains("HTTP proxy listening on") || line.contains("SOCKS5 proxy listening on")
    }
}
