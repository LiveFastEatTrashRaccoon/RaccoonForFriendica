package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

internal class DefaultTagsService(private val baseUrl: String, private val client: HttpClient) : TagsService {
    override suspend fun getFollowedTags(maxId: String?): Pair<List<Tag>, String?> {
        val response = client.get("$baseUrl/v1/followed_tags") {
            parameter("max_id", maxId)
        }
        val data: List<Tag> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return data to cursor
    }

    override suspend fun follow(name: String): Tag = client.post("$baseUrl/v1/tags/$name/follow").body()

    override suspend fun unfollow(name: String): Tag = client.post("$baseUrl/v1/tags/$name/unfollow").body()

    override suspend fun get(name: String): Tag = client.get("$baseUrl/v1/tags/$name").body()
}
