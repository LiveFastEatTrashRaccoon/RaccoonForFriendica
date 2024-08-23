package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType

sealed interface NotificationsPaginationSpecification {
    data class Default(
        val types: List<NotificationType>,
        val includeNsfw: Boolean = true,
    ) : NotificationsPaginationSpecification
}
