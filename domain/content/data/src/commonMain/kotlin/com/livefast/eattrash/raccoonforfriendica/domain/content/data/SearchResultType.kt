package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface SearchResultType {
    data object Posts : SearchResultType

    data object Users : SearchResultType

    data object Hashtags : SearchResultType
}
