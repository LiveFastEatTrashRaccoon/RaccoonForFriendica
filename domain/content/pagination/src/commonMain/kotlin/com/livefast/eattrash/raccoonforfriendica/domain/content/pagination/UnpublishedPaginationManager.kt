package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface UnpublishedPaginationManager {
    val canFetchMore: Boolean
    val history: List<TimelineEntryModel>

    suspend fun reset(specification: UnpublishedPaginationSpecification)

    suspend fun loadNextPage(): List<TimelineEntryModel>
}
