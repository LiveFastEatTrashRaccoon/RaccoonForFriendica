package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

internal class DefaultSettingsProvider(private val sharedPreferencesProvider: SharedPreferencesProvider) :
    SettingsProvider {
    override fun provide(): Settings = SharedPreferencesSettings(
        delegate = sharedPreferencesProvider.sharedPreferences,
        commit = false,
    )
}
