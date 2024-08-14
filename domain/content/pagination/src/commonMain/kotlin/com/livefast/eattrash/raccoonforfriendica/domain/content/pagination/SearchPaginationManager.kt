package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel

interface SearchPaginationManager {
    val canFetchMore: Boolean

    suspend fun reset(specification: SearchPaginationSpecification)

    suspend fun loadNextPage(): List<ExploreItemModel>
}
