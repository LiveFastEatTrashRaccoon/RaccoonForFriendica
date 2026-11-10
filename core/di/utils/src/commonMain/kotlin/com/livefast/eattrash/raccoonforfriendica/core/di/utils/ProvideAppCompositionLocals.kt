package com.livefast.eattrash.raccoonforfriendica.core.di.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Density
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toScaleFactor
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.toLanguageDirection
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources

@Composable
fun ProvideAppCompositionLocals(
    uiDeps: UiDeps? = null,
    lang: String? = null,
    fontScale: UiFontScale? = null,
    uriHandler: UriHandler? = null,
    content: @Composable () -> Unit,
) {
    val defaultDensity = LocalDensity.current
    val scaleFactor = fontScale?.toScaleFactor() ?: 1f
    val targetDensity = remember(defaultDensity, scaleFactor) {
        if (scaleFactor == 1f) {
            defaultDensity
        } else {
            Density(
                density = defaultDensity.density,
                fontScale = defaultDensity.fontScale * scaleFactor,
            )
        }
    }

    val providers = buildList<ProvidedValue<*>> {
        uiDeps?.let { deps ->
            add(LocalUiDeps provides deps)
            add(LocalResources provides deps.resources)
            add(LocalStrings provides deps.strings)
        }
        if (lang != null) {
            add(LocalLayoutDirection provides lang.toLanguageDirection())
        }
        if (scaleFactor != 1f) {
            add(LocalDensity provides targetDensity)
        }
        if (uriHandler != null) {
            add(LocalUriHandler provides uriHandler)
        }
    }

    CompositionLocalProvider(
        values = providers.toTypedArray(),
        content = content,
    )
}
