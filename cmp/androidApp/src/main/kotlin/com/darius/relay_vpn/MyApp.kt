package com.darius.relay_vpn

import android.app.Application
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initKoin(applicationContext)
    }
}
