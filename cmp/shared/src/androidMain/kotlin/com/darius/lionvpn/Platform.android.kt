package com.darius.lionvpn

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import com.darius.lionvpn.connectivity.NetworkHelper
import org.koin.mp.KoinPlatform.getKoin

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val type: Platform.Type = Platform.Type.ANDROID
    override fun isAndroid(): Boolean = true
}

actual fun getPlatform(): Platform = AndroidPlatform()

class AndroidApplicationComponent(
    val networkHelper: NetworkHelper,
)

actual fun isDebugBuild(): Boolean =
    (getKoin().get<ContextFactory>().getContext() as Context)
        .applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
