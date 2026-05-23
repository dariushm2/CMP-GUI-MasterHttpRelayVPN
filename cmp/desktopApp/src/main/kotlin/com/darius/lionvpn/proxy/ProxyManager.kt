package com.darius.lionvpn.proxy

interface ProxyManager {
    fun enableProxy(port: Int = 8085)
    fun disableProxy()
}
