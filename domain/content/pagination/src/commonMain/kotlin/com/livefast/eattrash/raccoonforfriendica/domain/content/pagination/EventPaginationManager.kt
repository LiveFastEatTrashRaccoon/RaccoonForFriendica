package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel

interface EventPaginationManager {
    val canFetchMore: Boolean

    suspend fun reset(specification: EventsPaginationSpecification)

    suspend fun loadNextPage(): List<EventModel>
}
