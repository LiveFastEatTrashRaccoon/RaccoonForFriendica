package com.github.akesiseli.raccoonforfriendica.core.appearance.di

import com.github.akesiseli.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.github.akesiseli.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.github.akesiseli.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import com.github.akesiseli.raccoonforfriendica.core.appearance.theme.DefaultBarColorProvider
import com.github.akesiseli.raccoonforfriendica.core.appearance.theme.DefaultColorSchemeProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

actual fun getThemeRepository(): ThemeRepository = CoreAppearanceHelper.repository

actual val nativeAppearanceModule =
    module {
        single<ColorSchemeProvider> {
            DefaultColorSchemeProvider()
        }
        single<BarColorProvider> {
            DefaultBarColorProvider()
        }
    }

actual fun getColorSchemeProvider(): ColorSchemeProvider = CoreAppearanceHelper.colorSchemeProvider

actual fun getBarColorProvider(): BarColorProvider = CoreAppearanceHelper.barColorProvider

internal object CoreAppearanceHelper : KoinComponent {
    val repository: ThemeRepository by inject()
    val colorSchemeProvider: ColorSchemeProvider by inject()
    val barColorProvider: BarColorProvider by inject()
}
