package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.DefaultTemporaryKeyStore
import com.livefast.eattrash.raccoonforfriendica.core.preferences.TemporaryKeyStore
import org.koin.dsl.module

val corePreferencesModule =
    module {
        includes(nativePreferencesModule)

        single<TemporaryKeyStore> {
            DefaultTemporaryKeyStore(
                settings = get(),
            )
        }
    }
