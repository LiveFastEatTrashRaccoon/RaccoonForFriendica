package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import android.annotation.SuppressLint
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

internal class DefaultSettingsProvider(
    private val preferencesProvider: SharedPreferencesProvider,
    private val legacyPreferencesProvider: SharedPreferencesProvider,
    private val encryptionHelper: EncryptionHelper,
) : SettingsProvider {
    override fun provide(): Settings {
        val legacyPreferences = legacyPreferencesProvider.provide()
        val newPreferences = preferencesProvider.provide()

        migrate(
            from = legacyPreferences,
            to = newPreferences,
            encryptionHelper = encryptionHelper,
        )

        if (REMOVE_OLD) {
            @SuppressLint("UseKtx")
            legacyPreferences.edit().clear().apply()
        }

        return if (ENABLE_NEW) {
            EncryptingSharedPreferencesSettings(
                preferences = newPreferences,
                encryptionHelper = encryptionHelper,
            )
        } else {
            SharedPreferencesSettings(
                delegate = legacyPreferences,
                commit = false,
            )
        }
    }

    companion object {
        private const val ENABLE_NEW = true
        private const val REMOVE_OLD = false
    }
}
