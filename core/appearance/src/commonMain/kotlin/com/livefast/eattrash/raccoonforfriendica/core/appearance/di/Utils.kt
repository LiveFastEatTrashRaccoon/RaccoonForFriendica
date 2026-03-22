package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import org.kodein.di.instance

fun getThemeRepository(): ThemeRepository {
    val res by RootDI.di.instance<ThemeRepository>()
    return res
}

@Composable
fun rememberThemeRepository() = remember { getThemeRepository() }

fun getColorSchemeProvider(): ColorSchemeProvider {
    val res by RootDI.di.instance<ColorSchemeProvider>()
    return res
}

@Composable
fun rememberColorSchemeProvider() = remember { getColorSchemeProvider() }

fun getBarColorProvider(): BarColorProvider {
    val res by RootDI.di.instance<BarColorProvider>()
    return res
}

@Composable
fun rememberBarColorProvider() = remember { getBarColorProvider() }
