package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection

fun getThemeRepository(): ThemeRepository = getByInjection(ThemeRepository::class)

@Composable
fun rememberThemeRepository() = remember { getThemeRepository() }

fun getColorSchemeProvider(): ColorSchemeProvider = getByInjection(ColorSchemeProvider::class)

@Composable
fun rememberColorSchemeProvider() = remember { getColorSchemeProvider() }

fun getBarColorProvider(): BarColorProvider = getByInjection(BarColorProvider::class)

@Composable
fun rememberBarColorProvider() = remember { getBarColorProvider() }
