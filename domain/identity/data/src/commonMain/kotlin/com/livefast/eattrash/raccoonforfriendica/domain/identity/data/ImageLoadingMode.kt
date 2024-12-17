package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

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

fun ImageLoadingMode.toInt(): Int =
    when (this) {
        ImageLoadingMode.Always -> 1
        ImageLoadingMode.OnWifi -> 2
        else -> 0
    }

fun Int.toImageLoadingMode(): ImageLoadingMode =
    when (this) {
        2 -> ImageLoadingMode.OnWifi
        1 -> ImageLoadingMode.Always
        else -> ImageLoadingMode.OnDemand
    }
