package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module

private const val DEFAULT_NAME = "secret_shared_prefs"

internal actual val nativePreferencesModule =
    module {
        single<Settings> {
            @OptIn(ExperimentalSettingsImplementation::class)
            KeychainSettings(service = DEFAULT_NAME)
        }
    }
