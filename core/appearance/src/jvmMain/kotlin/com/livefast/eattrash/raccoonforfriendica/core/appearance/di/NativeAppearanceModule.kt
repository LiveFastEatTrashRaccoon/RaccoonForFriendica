package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.DefaultBarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.DefaultColorSchemeProvider
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

actual val nativeAppearanceModule =
    DI.Module("NativeAppearanceModule") {
        bind<BarColorProvider> { singleton { DefaultBarColorProvider() } }
        bind<ColorSchemeProvider> { singleton { DefaultColorSchemeProvider() } }
    }
