package com.darius.relay_vpn

import android.content.Context
import com.darius.relay_vpn.data.di.networkModule
import com.darius.relay_vpn.domain.di.domainModule
import com.darius.relay_vpn.ui.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initKoin(
    context: Context,
) {
    startKoin {
        androidContext(context)
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
