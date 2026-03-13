package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.DefaultEncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultPreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.PreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal actual val nativePreferencesModule =
    DI.Module("NativePreferencesModule") {
        bind<PreferencesProvider> {
            singleton {
                DefaultPreferencesProvider()
            }
        }
        bind<EncryptionHelper> {
            singleton {
                DefaultEncryptionHelper()
            }
        }
        bind<SettingsProvider> {
            singleton {
                DefaultSettingsProvider(
                    provider = instance(),
                    encryptionHelper = instance(),
                )
            }
        }
    }
