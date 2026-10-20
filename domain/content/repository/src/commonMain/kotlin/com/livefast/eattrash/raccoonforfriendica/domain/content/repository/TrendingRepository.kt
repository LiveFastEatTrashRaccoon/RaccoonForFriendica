package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel

interface TrendingRepository {
    suspend fun getPosts(offset: Int, otherInstance: String? = null): List<PostModel>?

    suspend fun getHashtags(offset: Int, refresh: Boolean = false, otherInstance: String? = null): List<TagModel>?

    suspend fun getLinks(offset: Int, otherInstance: String? = null): List<LinkModel>?
}
