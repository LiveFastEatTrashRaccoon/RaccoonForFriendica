package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface CircleReplyPolicy {
    data object List : CircleReplyPolicy

    data object Followed : CircleReplyPolicy

    data object None : CircleReplyPolicy
}
