package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel

interface TimelinePaginationManager {
    val canFetchMore: Boolean
    val history: List<PostModel>

    suspend fun reset(specification: TimelinePaginationSpecification)

    suspend fun loadNextPage(): List<PostModel>

    suspend fun restoreHistory(values: List<PostModel>)

    fun extractState(): TimelinePaginationManagerState

    fun restoreState(state: TimelinePaginationManagerState)
}
