package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel

interface TagRepository {
    suspend fun getFollowed(): List<TagModel>

    suspend fun getBy(name: String): TagModel?

    suspend fun follow(name: String): TagModel?

    suspend fun unfollow(name: String): TagModel?
}
