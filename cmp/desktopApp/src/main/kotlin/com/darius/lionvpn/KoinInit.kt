package com.darius.lionvpn

import com.darius.lionvpn.data.di.networkModule
import com.darius.lionvpn.domain.di.domainModule
import com.darius.lionvpn.ui.di.appModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            listOf(
                appComponentModule,
                appModule,
                networkModule,
                domainModule,
                viewModelModule,
            )
        )
    }
}
