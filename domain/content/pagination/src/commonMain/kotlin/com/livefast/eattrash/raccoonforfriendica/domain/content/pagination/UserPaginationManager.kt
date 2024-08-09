package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface UserPaginationManager {
    val canFetchMore: Boolean

    suspend fun reset(specification: UserPaginationSpecification)

    suspend fun loadNextPage(): List<UserModel>
}
