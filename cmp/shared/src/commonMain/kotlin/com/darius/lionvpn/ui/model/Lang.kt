package com.darius.lionvpn.ui.model

enum class Lang(val label: String) {
    EN("en"),
    FA("fa");

    fun Lang.isEnglish() = this == Lang.EN
}
