package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor

interface TagRepository {
    suspend fun getFollowed(pageCursor: String? = null): ListWithPageCursor<TagModel>?

    suspend fun getBy(name: String): TagModel?

    suspend fun follow(name: String): TagModel?

    suspend fun unfollow(name: String): TagModel?
}
