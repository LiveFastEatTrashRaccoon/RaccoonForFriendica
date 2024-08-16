package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface TimelinePaginationManager {
    val canFetchMore: Boolean
    val history: List<TimelineEntryModel>

    suspend fun reset(specification: TimelinePaginationSpecification)

    suspend fun loadNextPage(): List<TimelineEntryModel>

    fun extractState(): TimelinePaginationManagerState

    fun restoreState(state: TimelinePaginationManagerState)
}
