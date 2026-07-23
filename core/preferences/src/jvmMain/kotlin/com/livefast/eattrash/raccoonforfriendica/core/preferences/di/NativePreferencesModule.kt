package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.DefaultEncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultPreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.DefaultSettingsProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.PreferencesProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import org.koin.dsl.module

internal actual val nativePreferencesModule = module {
    single<PreferencesProvider> {
        DefaultPreferencesProvider()
    }
    single<EncryptionHelper> {
        DefaultEncryptionHelper()
    }
    single<SettingsProvider> {
        DefaultSettingsProvider(
            provider = get(),
            encryptionHelper = get(),
        )
    }
}
