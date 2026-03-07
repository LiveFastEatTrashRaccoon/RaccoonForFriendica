package com.livefast.eattrash.raccoonforfriendica.core.appearance.theme

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme

internal class DefaultBarColorProvider : BarColorProvider {
    override val isBarThemeSupported = false

    @Composable
    override fun setBarColorAccordingToTheme(theme: UiTheme, barTheme: UiBarTheme) {
        // no-op
    }
}
