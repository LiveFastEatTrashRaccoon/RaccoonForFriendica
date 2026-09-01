package com.livefast.eattrash.raccoonforfriendica.core.appearance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toScaleFactor

@Composable
fun ProvideCustomFontScale(fontScale: UiFontScale?, content: @Composable () -> Unit) {
    val defaultDensity = LocalDensity.current
    val scaleFactor = fontScale?.toScaleFactor() ?: 1f
    val targetDensity = Density(density = defaultDensity.density, fontScale = defaultDensity.fontScale * scaleFactor)
    CompositionLocalProvider(
        value = LocalDensity provides targetDensity,
        content = content,
    )
}
