package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel

interface FollowedHashtagCache {
    suspend fun getAll(): List<TagModel>

    suspend fun refresh()

    suspend fun clear()

    suspend fun isFollowed(tag: TagModel): Boolean
}
