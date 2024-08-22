package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toScaleFactor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.getSettingsRepository

@Composable
fun ProvideCustomFontScale(content: @Composable () -> Unit) {
    val defaultDensity = LocalDensity.current
    val settingsRepository = remember { getSettingsRepository() }
    val currentSettings by settingsRepository.current.collectAsState()
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
