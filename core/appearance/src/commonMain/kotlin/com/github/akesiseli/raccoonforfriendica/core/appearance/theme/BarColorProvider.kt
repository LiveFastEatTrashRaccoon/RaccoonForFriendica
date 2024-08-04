package com.github.akesiseli.raccoonforfriendica.core.appearance.theme

import androidx.compose.runtime.Composable
import com.github.akesiseli.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.github.akesiseli.raccoonforfriendica.core.appearance.data.UiTheme

interface BarColorProvider {
    @Composable
    fun setBarColorAccordingToTheme(
        theme: UiTheme,
        barTheme: UiBarTheme,
    )
}
