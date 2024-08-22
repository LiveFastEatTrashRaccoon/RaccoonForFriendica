package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

sealed interface UrlOpeningMode {
    data object External : UrlOpeningMode

    data object CustomTabs : UrlOpeningMode
}

fun Int.toUrlOpeningMode(): UrlOpeningMode =
    when (this) {
        1 -> UrlOpeningMode.CustomTabs
        else -> UrlOpeningMode.External
}

fun UrlOpeningMode.toInt(): Int =
    when (this) {
        UrlOpeningMode.CustomTabs -> 1
        UrlOpeningMode.External -> 0
}
