package com.livefast.eattrash.raccoonforfriendica.core.appearance.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val themeState by repository.theme.collectAsStateWithLifecycle()
    val customSeedColor by repository.customSeedColor.collectAsStateWithLifecycle()
    val colorScheme =
        colorSchemeProvider.getColorScheme(
            theme = themeState,
            dynamic = useDynamicColors,
            customSeed = customSeedColor,
            isSystemInDarkTheme = isSystemInDarkTheme(),
        )

    val fontFamily by repository.fontFamily.collectAsStateWithLifecycle()
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
