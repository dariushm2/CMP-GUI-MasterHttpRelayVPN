package com.darius.lionvpn

import android.content.Context
import com.darius.lionvpn.data.di.networkModule
import com.darius.lionvpn.domain.di.domainModule
import com.darius.lionvpn.ui.di.appModule
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
