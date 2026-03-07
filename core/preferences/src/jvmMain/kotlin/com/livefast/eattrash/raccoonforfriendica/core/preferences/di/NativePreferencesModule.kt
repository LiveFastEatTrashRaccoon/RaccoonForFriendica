package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal actual val nativePreferencesModule =
    DI.Module("NativePreferencesModule") {
        bind<SettingsProvider> {
            singleton {
                DefaultSettingsProvider()
            }
        }
    }
