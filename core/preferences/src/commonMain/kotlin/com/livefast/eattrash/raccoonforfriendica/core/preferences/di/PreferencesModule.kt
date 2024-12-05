package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import com.russhwolf.settings.Settings
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.preferences.settings")
internal class SettingsWrapperModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.preferences.store")
internal class StoreModule

@Module
internal class SettingsModule {
    @Single
    fun provideSettings(provider: SettingsProvider): Settings = provider.provide()
}

@Module(
    includes = [
        ProviderModule::class,
        SettingsModule::class,
        SettingsWrapperModule::class,
        StoreModule::class,
    ],
)
class PreferencesModule
