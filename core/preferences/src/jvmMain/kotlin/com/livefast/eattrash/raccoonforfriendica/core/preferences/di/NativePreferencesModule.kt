package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.DefaultEncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultPreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.PreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal actual val nativePreferencesModule =
    DI.Module("NativePreferencesModule") {
        bindSingleton<PreferencesProvider> {
            DefaultPreferencesProvider()
        }
        bindSingleton<EncryptionHelper> {
            DefaultEncryptionHelper()
        }
        bindSingleton<SettingsProvider> {
            DefaultSettingsProvider(
                provider = instance(),
                encryptionHelper = instance(),
            )
        }
    }
