package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.settings.DefaultSettingsWrapper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.settings.SettingsWrapper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.DefaultTemporaryKeyStore
import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val preferencesModule =
    DI.Module("PreferencesModule") {
        import(nativePreferencesModule)

        bind<SettingsWrapper> {
            singleton {
                val provider = instance<SettingsProvider>()
                DefaultSettingsWrapper(settings = provider.provide())
            }
        }
        bind<TemporaryKeyStore> {
            singleton {
                DefaultTemporaryKeyStore(
                    settings = instance(),
                )
            }
        }
    }
