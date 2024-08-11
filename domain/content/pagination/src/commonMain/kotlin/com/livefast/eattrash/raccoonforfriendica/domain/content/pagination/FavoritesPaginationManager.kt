package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface FavoritesPaginationManager {
    val canFetchMore: Boolean

    suspend fun reset(specification: FavoritesPaginationSpecification)

    suspend fun loadNextPage(): List<TimelineEntryModel>
}
