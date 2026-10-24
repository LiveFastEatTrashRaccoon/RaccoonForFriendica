package com.livefast.eattrash.raccoonforfriendica.core.di.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Density
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toScaleFactor
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Locales
import com.livefast.eattrash.raccoonforfriendica.core.l10n.toLanguageDirection
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources

@Composable
fun ProvideAppCompositionLocals(
    uiDeps: UiDeps,
    lang: String = Locales.EN,
    fontScale: UiFontScale? = null,
    uriHandler: UriHandler? = null,
    content: @Composable () -> Unit,
) {
    val defaultDensity = LocalDensity.current
    val scaleFactor = fontScale?.toScaleFactor() ?: 1f
    val targetDensity = Density(density = defaultDensity.density, fontScale = defaultDensity.fontScale * scaleFactor)

    val providers = buildList<ProvidedValue<*>> {
        add(LocalUiDeps provides uiDeps)
        add(LocalResources provides uiDeps.resources)
        add(LocalStrings provides uiDeps.strings)
        add(LocalLayoutDirection provides lang.toLanguageDirection())
        add(LocalDensity provides targetDensity)
        uriHandler?.let { add(LocalUriHandler provides it) }
    }

    CompositionLocalProvider(
        values = providers.toTypedArray(),
        content = content,
    )
}
