package com.darius.lionvpn

import com.darius.lionvpn.connectivity.ConnectivityHandler
import com.darius.lionvpn.connectivity.NetworkHelper
import com.darius.lionvpn.proxy.LinuxProxyManager
import com.darius.lionvpn.proxy.MacosProxyManager
import com.darius.lionvpn.proxy.ProxyManager
import com.darius.lionvpn.proxy.WindowsProxyManager
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appComponentModule = module {
    single {
        ContextFactory()
    }
    single<NetworkHelper> {
        JvmNetworkHelper()
    }
    single {
        ConnectivityHandler(get())
    }
    single<ProxyManager> {
        when (JvmPlatform().os) {
            JvmPlatform.OS.WIN -> WindowsProxyManager
            JvmPlatform.OS.MAC -> MacosProxyManager
            JvmPlatform.OS.LINUX -> LinuxProxyManager
        }
    }
    viewModel {
        AppViewModel()
    }
}
