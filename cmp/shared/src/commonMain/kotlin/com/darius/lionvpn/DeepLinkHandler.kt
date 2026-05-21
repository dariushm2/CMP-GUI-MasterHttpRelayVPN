package com.darius.lionvpn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DeepLinkHandler {
    private var _deepLink = MutableStateFlow<String?>(null)
    val deeplink: StateFlow<String?> = _deepLink

    fun setDeepLink(url: String?) {
        _deepLink.value = url?.substringAfter(Constants.SCHEME)
    }
}
