package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.DefaultThemeColorRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.DefaultThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeColorRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import org.koin.dsl.module

val appearanceModule = module {
    includes(nativeAppearanceModule)
    single<ThemeColorRepository> {
        DefaultThemeColorRepository()
    }
    single<ThemeRepository> {
        DefaultThemeRepository()
    }
}
