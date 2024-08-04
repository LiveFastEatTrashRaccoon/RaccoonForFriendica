package com.github.akesiseli.raccoonforfriendica.domain.content.pagination

import com.github.akesiseli.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface TimelinePaginationManager {
    val canFetchMore: Boolean

    suspend fun reset(specification: TimelinePaginationSpecification)

    suspend fun loadNextPage(): List<TimelineEntryModel>
}
