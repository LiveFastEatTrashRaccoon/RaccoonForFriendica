package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface ImageLoadingMode {
    data object OnDemand : ImageLoadingMode

    data object Always : ImageLoadingMode

    data object OnWifi : ImageLoadingMode
}

@Composable
fun ImageLoadingMode.toReadableName(): String =
    when (this) {
        ImageLoadingMode.OnWifi -> LocalStrings.current.imageLoadingModeOnWiFi
        ImageLoadingMode.Always -> LocalStrings.current.imageLoadingModeAlways
        ImageLoadingMode.OnDemand -> LocalStrings.current.imageLoadingModeOnDemand
    }
