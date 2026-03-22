package com.livefast.eattrash.raccoonforfriendica.core.appearance.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.getBarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.getColorSchemeProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.getThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.rememberBarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.rememberColorSchemeProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.rememberThemeRepository

@Composable
fun AppTheme(useDynamicColors: Boolean, barTheme: UiBarTheme, content: @Composable () -> Unit) {
    val repository = rememberThemeRepository()
    val themeState by repository.theme.collectAsState()
    val customSeedColor by repository.customSeedColor.collectAsState()
    val colorSchemeProvider = rememberColorSchemeProvider()
    val colorScheme =
        colorSchemeProvider.getColorScheme(
            theme = themeState,
            dynamic = useDynamicColors,
            customSeed = customSeedColor,
            isSystemInDarkTheme = isSystemInDarkTheme(),
        )

    val fontFamily by repository.fontFamily.collectAsState()
    val typography = fontFamily.toTypography()

    val barColorProvider = rememberBarColorProvider()
    barColorProvider.setBarColorAccordingToTheme(
        theme = themeState,
        barTheme = barTheme,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}
