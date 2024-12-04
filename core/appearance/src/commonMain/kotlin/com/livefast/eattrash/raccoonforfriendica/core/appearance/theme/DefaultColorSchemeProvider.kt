package com.livefast.eattrash.raccoonforfriendica.core.appearance.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import org.koin.core.annotation.Single

@Single
internal expect class DefaultColorSchemeProvider : ColorSchemeProvider {
    override val supportsDynamicColors: Boolean

    override fun getColorScheme(
        theme: UiTheme,
        dynamic: Boolean,
        customSeed: Color?,
        isSystemInDarkTheme: Boolean,
    ): ColorScheme
}
