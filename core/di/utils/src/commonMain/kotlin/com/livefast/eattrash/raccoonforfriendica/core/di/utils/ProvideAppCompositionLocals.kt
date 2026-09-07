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

@Composable
fun ProvideAppCompositionLocals(
    uiDeps: UiDeps,
    lang: String = Locales.EN,
    uriHandler: UriHandler? = null,
    content: @Composable () -> Unit,
) {
    val providers = buildList<ProvidedValue<*>> {
        add(LocalUiDeps provides uiDeps)
        add(LocalResources provides uiDeps.resources)
        add(LocalStrings provides uiDeps.strings)
        add(LocalLayoutDirection provides lang.toLanguageDirection())
        uriHandler?.let { add(LocalUriHandler provides it) }
    }

    CompositionLocalProvider(
        values = providers.toTypedArray(),
        content = content,
    )
}
