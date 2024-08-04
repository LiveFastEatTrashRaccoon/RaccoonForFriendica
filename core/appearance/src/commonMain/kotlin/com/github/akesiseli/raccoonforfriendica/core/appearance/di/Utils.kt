package com.github.akesiseli.raccoonforfriendica.core.appearance.di

import com.github.akesiseli.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.github.akesiseli.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.github.akesiseli.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import org.koin.core.module.Module

expect val nativeAppearanceModule: Module

expect fun getThemeRepository(): ThemeRepository

expect fun getColorSchemeProvider(): ColorSchemeProvider

expect fun getBarColorProvider(): BarColorProvider
