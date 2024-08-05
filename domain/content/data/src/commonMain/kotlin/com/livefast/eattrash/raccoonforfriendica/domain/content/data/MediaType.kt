package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface MediaType {
    data object Audio : MediaType

    data object Video : MediaType

    data object Image : MediaType

    data object Unknown : MediaType
}
