package com.darius.lionvpn.ui.model

enum class Lang(val label: String) {
    EN("en"),
    FA("fa");

    companion object {
        fun Lang.isEnglish() = this == EN
        fun Lang.isPersian() = this == FA
    }
}
