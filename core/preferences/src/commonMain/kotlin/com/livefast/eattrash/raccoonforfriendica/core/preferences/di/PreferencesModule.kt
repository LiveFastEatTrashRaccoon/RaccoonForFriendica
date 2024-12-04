package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import com.russhwolf.settings.Settings
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.preferences.settings")
internal class SettingsModule {
    @Single
    fun provideSettings(provider: SettingsProvider): Settings = provider.provide()
}

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.preferences.store")
internal class StoreModule

@Module(includes = [SettingsModule::class, StoreModule::class])
internal class PreferencesModule

val corePreferencesModule = PreferencesModule().module
