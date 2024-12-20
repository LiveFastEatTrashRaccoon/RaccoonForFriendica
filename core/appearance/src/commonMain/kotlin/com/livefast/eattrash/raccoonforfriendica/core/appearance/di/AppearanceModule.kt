package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.DefaultThemeColorRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.DefaultThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeColorRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

val appearanceModule =
    DI.Module("AppearanceModule") {
        import(nativeAppearanceModule)
        bind<ThemeColorRepository> { singleton { DefaultThemeColorRepository() } }
        bind<ThemeRepository> { singleton { DefaultThemeRepository() } }
    }
