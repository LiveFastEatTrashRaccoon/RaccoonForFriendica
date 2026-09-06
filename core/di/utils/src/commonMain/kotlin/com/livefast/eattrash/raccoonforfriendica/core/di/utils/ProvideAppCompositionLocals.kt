package com.livefast.eattrash.raccoonforfriendica.core.di.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Locales
import com.livefast.eattrash.raccoonforfriendica.core.l10n.toLanguageDirection
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

@Composable
fun ProvideAppCompositionLocals(
    uiDeps: UiDeps,
    lang: String = Locales.EN,
    metroViewModelFactory: MetroViewModelFactory? = null,
    uriHandler: UriHandler? = null,
    content: @Composable () -> Unit,
) {
    val providers = buildList<ProvidedValue<*>> {
        add(LocalUiDeps provides uiDeps)
        add(LocalResources provides uiDeps.resources)
        add(LocalStrings provides uiDeps.strings)
        add(LocalLayoutDirection provides lang.toLanguageDirection())
        metroViewModelFactory?.let { add(LocalMetroViewModelFactory provides it) }
        uriHandler?.let { add(LocalUriHandler provides it) }
    }

    CompositionLocalProvider(
        values = providers.toTypedArray(),
        content = content,
    )
}
