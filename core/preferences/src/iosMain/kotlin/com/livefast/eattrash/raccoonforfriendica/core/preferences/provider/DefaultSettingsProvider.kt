package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import org.koin.core.annotation.Single

@Single
internal actual class DefaultSettingsProvider : SettingsProvider {
    @OptIn(ExperimentalSettingsImplementation::class)
    actual override fun provide(): Settings = KeychainSettings(service = DEFAULT_NAME)

    companion object {
        private const val DEFAULT_NAME = "secret_shared_prefs"
    }
}
