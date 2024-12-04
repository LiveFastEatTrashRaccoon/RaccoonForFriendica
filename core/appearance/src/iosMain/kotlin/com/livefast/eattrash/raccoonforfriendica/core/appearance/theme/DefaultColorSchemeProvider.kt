package com.livefast.eattrash.raccoonforfriendica.core.appearance.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme
import org.koin.core.annotation.Single

@Single
internal actual class DefaultColorSchemeProvider : ColorSchemeProvider {
    actual override val supportsDynamicColors = false

    actual override fun getColorScheme(
        theme: UiTheme,
        dynamic: Boolean,
        customSeed: Color?,
        isSystemInDarkTheme: Boolean,
    ): ColorScheme =
        when (theme) {
            UiTheme.Dark -> {
                if (customSeed != null) {
                    dynamicColorScheme(
                        seedColor = customSeed,
                        isDark = true,
                        isAmoled = false,
                        style = defaultStyle,
                    )
                } else {
                    DarkColors
                }
            }

            UiTheme.Black -> {
                if (customSeed != null) {
                    dynamicColorScheme(
                        seedColor = customSeed,
                        isDark = true,
                        isAmoled = true,
                        style = defaultStyle,
                    )
                } else {
                    BlackColors
                }
            }

            UiTheme.Light ->
                if (customSeed != null) {
                    dynamicColorScheme(
                        seedColor = customSeed,
                        isDark = false,
                        isAmoled = false,
                        style = defaultStyle,
                    )
                } else {
                    LightColors
                }

            else -> {
                if (customSeed != null) {
                    dynamicColorScheme(
                        seedColor = customSeed,
                        isDark = isSystemInDarkTheme,
                        isAmoled = false,
                        style = defaultStyle,
                    )
                } else {
                    if (isSystemInDarkTheme) {
                        DarkColors
                    } else {
                        LightColors
                    }
                }
            }
        }

    companion object {
        private val defaultStyle = PaletteStyle.Rainbow
    }
}
