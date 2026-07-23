package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.settings.DefaultSettingsWrapper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.settings.SettingsWrapper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.DefaultTemporaryKeyStore
import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import org.koin.dsl.module

val preferencesModule = module {
    includes(nativePreferencesModule)

    single<SettingsWrapper> {
        val provider = get<SettingsProvider>()
        DefaultSettingsWrapper(settings = provider.provide())
    }
    single<TemporaryKeyStore> {
        DefaultTemporaryKeyStore(
            settings = get(),
        )
    }
}
