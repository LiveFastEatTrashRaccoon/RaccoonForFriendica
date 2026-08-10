package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.russhwolf.settings.Settings
import org.koin.core.annotation.Single

@Single
internal class DefaultSettingsProvider(
    private val provider: PreferencesProvider,
    private val encryptionHelper: EncryptionHelper,
) : SettingsProvider {

    override fun provide(): Settings = EncryptingPreferencesSettings(
        preferences = provider.provide(),
        encryptionHelper = encryptionHelper,
    )
}
