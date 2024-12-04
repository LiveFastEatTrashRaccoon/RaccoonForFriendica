package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.annotation.Single

@Single
internal actual class DefaultSettingsProvider(
    private val sharedPreferencesProvider: SharedPreferencesProvider,
) : SettingsProvider {
    actual override fun provide(): Settings =
        SharedPreferencesSettings(
            delegate = sharedPreferencesProvider.sharedPreferences,
            commit = false,
        )
}
