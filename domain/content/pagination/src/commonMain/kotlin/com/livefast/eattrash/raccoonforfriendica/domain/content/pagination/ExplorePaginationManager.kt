package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel

interface ExplorePaginationManager {
    val canFetchMore: Boolean

    suspend fun reset(specification: ExplorePaginationSpecification)

    suspend fun loadNextPage(): List<ExploreItemModel>
}
