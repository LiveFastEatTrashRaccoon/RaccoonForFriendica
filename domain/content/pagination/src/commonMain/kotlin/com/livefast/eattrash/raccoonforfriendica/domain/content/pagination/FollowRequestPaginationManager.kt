package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface FollowRequestPaginationManager {
    val canFetchMore: Boolean

    suspend fun reset()

    suspend fun loadNextPage(): List<UserModel>
}
