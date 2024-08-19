package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

sealed interface UrlOpeningMode {
    data object External : UrlOpeningMode

    data object CustomTabs : UrlOpeningMode
}
