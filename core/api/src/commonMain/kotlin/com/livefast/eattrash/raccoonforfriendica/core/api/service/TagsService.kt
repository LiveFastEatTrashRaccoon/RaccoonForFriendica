package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Page
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import io.ktor.client.HttpClient

interface TagsService {
    suspend fun getFollowedTags(maxId: String? = null): Page<Tag>

    suspend fun follow(name: String): Tag

    suspend fun unfollow(name: String): Tag

    suspend fun get(name: String): Tag
}

fun interface TagsServiceFactory {
    fun create(baseUrl: String, client: HttpClient): TagsService
}
