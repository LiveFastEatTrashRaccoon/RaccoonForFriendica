package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.appearance.theme")
internal actual class ThemeModule

actual fun getThemeRepository(): ThemeRepository = CoreAppearanceHelper.repository

actual fun getColorSchemeProvider(): ColorSchemeProvider = CoreAppearanceHelper.colorSchemeProvider

actual fun getBarColorProvider(): BarColorProvider = CoreAppearanceHelper.barColorProvider

internal object CoreAppearanceHelper : KoinComponent {
    val repository: ThemeRepository by inject()
    val colorSchemeProvider: ColorSchemeProvider by inject()
    val barColorProvider: BarColorProvider by inject()
}
