package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.DefaultEncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSharedPreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.LegacySharedPreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SharedPreferencesProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual val nativePreferencesModule = module {
    single<SharedPreferencesProvider>(named("default")) {
        DefaultSharedPreferencesProvider(context = get())
    }
    single<SharedPreferencesProvider>(named("legacy")) {
        LegacySharedPreferencesProvider(context = get())
    }
    single<SettingsProvider> {
        DefaultSettingsProvider(
            preferencesProvider = get(named("default")),
            legacyPreferencesProvider = get(named("legacy")),
            encryptionHelper = get(),
        )
    }
    single<EncryptionHelper> {
        DefaultEncryptionHelper()
    }
}
