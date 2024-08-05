package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface TimelinePaginationManager {
    val canFetchMore: Boolean

    suspend fun reset(specification: TimelinePaginationSpecification)

    suspend fun loadNextPage(): List<TimelineEntryModel>
}
