package com.darius.lionvpn.proxy

interface ProxyManager {
    fun enableProxy(host: String = "127.0.0.1", port: Int = 8085)
    fun disableProxy()
}
