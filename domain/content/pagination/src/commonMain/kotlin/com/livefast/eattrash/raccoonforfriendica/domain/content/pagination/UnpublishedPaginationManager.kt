package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel

interface UnpublishedPaginationManager {
    val canFetchMore: Boolean
    val history: List<PostModel>

    suspend fun reset(specification: UnpublishedPaginationSpecification)

    suspend fun loadNextPage(): List<PostModel>
}
