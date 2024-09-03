package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel

interface DirectMessagesPaginationManager {
    val canFetchMore: Boolean

    suspend fun reset(specification: DirectMessagesPaginationSpecification)

    suspend fun loadNextPage(): List<DirectMessageModel>
}
