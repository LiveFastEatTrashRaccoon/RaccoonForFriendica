package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.DefaultEncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSharedPreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.LegacySharedPreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SharedPreferencesProvider
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal actual val nativePreferencesModule =
    DI.Module("NativePreferencesModule") {
        bindSingleton<SharedPreferencesProvider>(tag = "default") {
            DefaultSharedPreferencesProvider(context = instance())
        }
        bindSingleton<SharedPreferencesProvider>(tag = "legacy") {
            LegacySharedPreferencesProvider(context = instance())
        }
        bindSingleton<SettingsProvider> {
            DefaultSettingsProvider(
                preferencesProvider = instance(tag = "default"),
                legacyPreferencesProvider = instance(tag = "legacy"),
                encryptionHelper = instance(),
            )
        }
        bindSingleton<EncryptionHelper> {
            DefaultEncryptionHelper()
        }
    }
