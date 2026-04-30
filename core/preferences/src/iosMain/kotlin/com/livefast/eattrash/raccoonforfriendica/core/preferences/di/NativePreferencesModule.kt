package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import org.kodein.di.DI
import org.kodein.di.bindSingleton

internal actual val nativePreferencesModule =
    DI.Module("NativePreferencesModule") {
        bindSingleton<SettingsProvider> {
            DefaultSettingsProvider()
        }
    }
