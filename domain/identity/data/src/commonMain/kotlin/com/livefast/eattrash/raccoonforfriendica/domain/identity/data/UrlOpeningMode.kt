package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface UrlOpeningMode {
    data object Internal : UrlOpeningMode

    data object External : UrlOpeningMode

    data object CustomTabs : UrlOpeningMode
}

fun Int.toUrlOpeningMode(): UrlOpeningMode = when (this) {
    2 -> UrlOpeningMode.Internal
    1 -> UrlOpeningMode.CustomTabs
    else -> UrlOpeningMode.External
}

fun UrlOpeningMode.toInt(): Int = when (this) {
    UrlOpeningMode.Internal -> 2
    UrlOpeningMode.CustomTabs -> 1
    UrlOpeningMode.External -> 0
}

@Composable
fun UrlOpeningMode.toReadableName(): String = when (this) {
    UrlOpeningMode.Internal -> LocalStrings.current.urlOpeningModeInternal
    UrlOpeningMode.CustomTabs -> LocalStrings.current.urlOpeningModeCustomTabs
    UrlOpeningMode.External -> LocalStrings.current.urlOpeningModeExternal
}
