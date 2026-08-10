package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.SettingsProvider
import com.russhwolf.settings.Settings
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(includes = [NativePreferencesModule::class])
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.preferences")
class PreferencesModule {
    @Single
    fun provideSettings(provider: SettingsProvider): Settings = provider.provide()
}
