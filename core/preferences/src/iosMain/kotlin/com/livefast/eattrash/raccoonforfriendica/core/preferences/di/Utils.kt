package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.DefaultSettingsWrapper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.SettingsWrapper
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

private const val DEFAULT_NAME = "secret_shared_prefs"

internal actual val nativePreferencesModule =
    module {
        single<Settings> { params ->
            val name: String? = params[0]
            @OptIn(ExperimentalSettingsImplementation::class)
            KeychainSettings(service = name ?: DEFAULT_NAME)
        }
        single<SettingsWrapper> {
            DefaultSettingsWrapper(settings = get(parameters = { parametersOf(DEFAULT_NAME) }))
        }
    }
