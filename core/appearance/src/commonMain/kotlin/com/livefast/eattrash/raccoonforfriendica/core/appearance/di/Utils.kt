package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import org.koin.core.annotation.Module

@Module
internal expect class ThemeModule()

expect fun getThemeRepository(): ThemeRepository

expect fun getColorSchemeProvider(): ColorSchemeProvider

expect fun getBarColorProvider(): BarColorProvider
