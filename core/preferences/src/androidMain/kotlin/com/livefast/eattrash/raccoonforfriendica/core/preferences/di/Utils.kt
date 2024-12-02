package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SharedPreferencesProvider
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.dsl.module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.preferences.provider")
internal class NativePreferencesModule {
    @Single
    fun provideSettings(sharedPreferencesProvider: SharedPreferencesProvider): Settings =
        SharedPreferencesSettings(
            delegate = sharedPreferencesProvider.sharedPreferences,
            commit = false,
        )
}

internal actual val nativePreferencesModule = NativePreferencesModule().module
