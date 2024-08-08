package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel

interface NotificationsPaginationManager {
    val canFetchMore: Boolean

    suspend fun reset(specification: NotificationsPaginationSpecification)

    suspend fun loadNextPage(): List<NotificationModel>
}
