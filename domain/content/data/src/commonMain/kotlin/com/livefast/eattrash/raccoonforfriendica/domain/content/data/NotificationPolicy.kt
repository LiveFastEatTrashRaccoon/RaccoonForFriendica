package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface NotificationPolicy {
    data object All : NotificationPolicy

    data object Followed : NotificationPolicy

    data object Follower : NotificationPolicy

    data object None : NotificationPolicy
}
