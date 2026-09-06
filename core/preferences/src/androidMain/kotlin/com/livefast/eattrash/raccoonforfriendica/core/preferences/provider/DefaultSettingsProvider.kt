package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import android.annotation.SuppressLint
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultSettingsProvider(
    @Named("default") private val preferencesProvider: SharedPreferencesProvider,
    @Named("legacy") private val legacyPreferencesProvider: SharedPreferencesProvider,
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
