package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel

interface FollowedHashtagsPaginationManager {
    val canFetchMore: Boolean

    suspend fun reset()

    suspend fun loadNextPage(): List<TagModel>
}
