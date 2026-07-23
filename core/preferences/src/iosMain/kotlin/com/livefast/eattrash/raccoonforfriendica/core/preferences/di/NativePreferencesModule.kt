package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import org.koin.dsl.module

internal actual val nativePreferencesModule = module {
    single<SettingsProvider> {
        DefaultSettingsProvider()
    }
}
