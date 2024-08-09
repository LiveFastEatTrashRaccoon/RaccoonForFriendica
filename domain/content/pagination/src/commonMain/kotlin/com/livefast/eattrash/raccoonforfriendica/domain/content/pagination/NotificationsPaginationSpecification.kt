package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType

sealed interface NotificationsPaginationSpecification {
    data class Default(
        val types: List<NotificationType> =
            listOf(
                NotificationType.Follow,
                NotificationType.FollowRequest,
                NotificationType.Mention,
                NotificationType.Entry,
                NotificationType.Update,
            ),
        val withRelationshipStatus: Boolean = false,
    ) : NotificationsPaginationSpecification
}
