package com.darius.lionvpn.model

sealed interface AndroidUiEffect {
    object ConnectVpn : AndroidUiEffect
    object CheckAndSaveCertificate : AndroidUiEffect
    object SaveSettings : AndroidUiEffect
    object UninstallCertificate : AndroidUiEffect
}
