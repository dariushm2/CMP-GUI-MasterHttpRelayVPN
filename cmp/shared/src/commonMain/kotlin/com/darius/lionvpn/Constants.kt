package com.darius.lionvpn

object Constants {
    const val SCHEME = "lionvpn://"
    const val BASE_URL = ""

    object Config {
        const val SCRIPT_ID = "script_id"
        const val SCRIPT_IDS = "script_ids"
        const val AUTH_KEY = "auth_key"
        const val FILE_NAME = "config.json"
        const val TEMPLATE_FILE_NAME = "config.example.json"
    }

    object Prefs {
        const val NAME = "vpn_config"
        const val KEY_SAVED_CONFIGS_JSON = "saved_configs_json"
        const val KEY_SELECTED_CONFIG_INDEX = "selected_config_index"
        const val KEY_SCRIPT_ID = "script_id"
        const val KEY_AUTH_KEY = "auth_key"
        const val KEY_RAW_CONFIG_JSON = "raw_config_json"
        const val KEY_LANGUAGE = "language"
    }
}
