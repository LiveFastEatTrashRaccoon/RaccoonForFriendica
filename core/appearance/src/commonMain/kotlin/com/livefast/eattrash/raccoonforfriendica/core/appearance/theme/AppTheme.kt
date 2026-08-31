package com.livefast.eattrash.raccoonforfriendica.core.appearance.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository

@Composable
fun AppTheme(
    repository: ThemeRepository,
    barColorProvider: BarColorProvider,
    colorSchemeProvider: ColorSchemeProvider,
    useDynamicColors: Boolean,
    barTheme: UiBarTheme,
    content: @Composable () -> Unit,
    ) {
    val themeState by repository.theme.collectAsState()
    val customSeedColor by repository.customSeedColor.collectAsState()
    val colorScheme =
        colorSchemeProvider.getColorScheme(
            theme = themeState,
            dynamic = useDynamicColors,
            customSeed = customSeedColor,
            isSystemInDarkTheme = isSystemInDarkTheme(),
        )

    val fontFamily by repository.fontFamily.collectAsState()
    val typography = fontFamily.toTypography()

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
