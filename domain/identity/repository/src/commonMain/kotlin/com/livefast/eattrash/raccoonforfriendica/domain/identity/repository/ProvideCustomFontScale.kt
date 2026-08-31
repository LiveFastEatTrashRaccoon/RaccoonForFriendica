package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toScaleFactor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel

@Composable
fun ProvideCustomFontScale(currentSettings: SettingsModel?, content: @Composable () -> Unit) {
    val defaultDensity = LocalDensity.current
    val scaleFactor = currentSettings?.fontScale?.toScaleFactor() ?: 1f
    CompositionLocalProvider(
        value =
        LocalDensity provides
            Density(
                density = defaultDensity.density,
                fontScale = defaultDensity.fontScale * scaleFactor,
            ),
        content = content,
    )
}
