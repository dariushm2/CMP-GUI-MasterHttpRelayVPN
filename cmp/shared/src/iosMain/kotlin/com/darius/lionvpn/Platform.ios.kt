package com.darius.lionvpn

import com.darius.lionvpn.connectivity.NetworkHelper
import platform.UIKit.UIDevice
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import kotlin.experimental.ExperimentalNativeApi

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() +
            " " +
            UIDevice.currentDevice.systemVersion
    override val type: Platform.Type = Platform.Type.IOS

    override fun isIos(): Boolean = true
}

actual fun getPlatform(): Platform = IOSPlatform()

class IosApplicationComponent(
    val networkHelper: NetworkHelper,
)

@OptIn(ExperimentalNativeApi::class)
actual fun isDebugBuild(): Boolean = kotlin.native.Platform.isDebugBinary

actual fun getCurrentTimeString(): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "HH:mm:ss"
    }
    return formatter.stringFromDate(NSDate())
}
