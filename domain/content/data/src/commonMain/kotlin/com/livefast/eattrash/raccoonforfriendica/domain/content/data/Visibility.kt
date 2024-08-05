package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface Visibility {
    data object Public : Visibility

    data object Unlisted : Visibility

    data object Private : Visibility

    data object Direct : Visibility
}
