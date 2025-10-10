package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.DefaultEncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSharedPreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.LegacySharedPreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SharedPreferencesProvider
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal actual val nativePreferencesModule =
    DI.Module("NativePreferencesModule") {
        bind<SharedPreferencesProvider>(tag = "default") {
            singleton {
                DefaultSharedPreferencesProvider(context = instance())
            }
        }
        bind<SharedPreferencesProvider>(tag = "legacy") {
            singleton {
                LegacySharedPreferencesProvider(context = instance())
            }
        }
        bind<SettingsProvider> {
            singleton {
                DefaultSettingsProvider(
                    preferencesProvider = instance(tag = "default"),
                    legacyPreferencesProvider = instance(tag = "legacy"),
                    encryptionHelper = instance(),
                )
            }
        }
        bind<EncryptionHelper> {
            singleton {
                DefaultEncryptionHelper()
            }
        }
    }
