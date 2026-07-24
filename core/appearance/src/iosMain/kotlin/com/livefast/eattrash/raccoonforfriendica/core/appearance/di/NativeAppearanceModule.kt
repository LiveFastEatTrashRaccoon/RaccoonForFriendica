package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.DefaultBarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.DefaultColorSchemeProvider
import org.koin.dsl.module

actual val nativeAppearanceModule = module {
    single<BarColorProvider> {
        DefaultBarColorProvider()
    }
    single<ColorSchemeProvider> {
        DefaultColorSchemeProvider()
    }
}
