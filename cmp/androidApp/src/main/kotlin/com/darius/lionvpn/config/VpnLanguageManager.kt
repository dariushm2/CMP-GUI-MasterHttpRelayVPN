package com.darius.lionvpn.config

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import com.darius.lionvpn.ui.model.Lang
import java.util.Locale

class VpnLanguageManager(private val preferencesManager: VpnPreferencesManager) {

    fun applyLocaleToContext(context: Context): Context {
        val lang = preferencesManager.loadLanguageFromPrefs()
        val locale = Locale(lang.label)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocales(LocaleList(locale))
        config.setLayoutDirection(locale)

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        return context.createConfigurationContext(config)
    }

    fun isCurrentLocaleDifferent(context: Context, targetLang: Lang): Boolean {
        val currentLocale = context.resources.configuration.locales[0]
        return currentLocale.language != targetLang.label
    }
}
