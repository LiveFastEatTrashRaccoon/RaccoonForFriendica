package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType

private val DEFAULT_TYPES =
    listOf(
        NotificationType.Follow,
        NotificationType.FollowRequest,
        NotificationType.Mention,
        NotificationType.Entry,
        NotificationType.Update,
        NotificationType.Poll,
)

sealed interface NotificationsPaginationSpecification {
    data class Default(
        val types: List<NotificationType> = DEFAULT_TYPES,
        val includeNsfw: Boolean = true,
    ) : NotificationsPaginationSpecification
}
