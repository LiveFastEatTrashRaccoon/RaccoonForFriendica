package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface PreviewType {
    data object Photo : PreviewType

    data object Video : PreviewType

    data object Link : PreviewType

    data object Unknown : PreviewType
}
