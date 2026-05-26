package com.darius.lionvpn

import com.darius.lionvpn.connectivity.ConnectivityHandler
import com.darius.lionvpn.connectivity.NetworkHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import com.darius.lionvpn.config.*

val appComponentModule = module {
    single {
        ContextFactory()
    }
    single {
        ConfigTemplateProvider(androidContext())
    }
    single {
        VpnPreferencesManager(androidContext())
    }
    single {
        VpnServiceManager(androidContext(), get())
    }
    single {
        VpnCertificateManager(androidContext())
    }
    single {
        VpnLanguageManager(get())
    }
    single<NetworkHelper> {
        AndroidNetworkHelper(androidContext())
    }
    single {
        ConnectivityHandler(get())
    }
    viewModel {
        AndroidAppViewModel()
    }
}
