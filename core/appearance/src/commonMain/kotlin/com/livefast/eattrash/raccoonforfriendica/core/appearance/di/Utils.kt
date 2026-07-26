package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection

@OptIn(DelicateDiApi::class)
@Composable
fun rememberThemeRepository() = remember { getByInjection(ThemeRepository::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberColorSchemeProvider() = remember { getByInjection(ColorSchemeProvider::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberBarColorProvider() = remember { getByInjection(BarColorProvider::class) }
