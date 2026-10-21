package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Page
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

@AssistedInject
class DefaultTagsService(@Assisted private val baseUrl: String, @Assisted private val client: HttpClient) :
    TagsService {

    override suspend fun getFollowedTags(maxId: String?): Page<Tag> {
        val response = client.get("$baseUrl/v1/followed_tags") {
            parameter("max_id", maxId)
        }
        val data: List<Tag> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun follow(name: String): Tag = client.post("$baseUrl/v1/tags/$name/follow").body()

    override suspend fun unfollow(name: String): Tag = client.post("$baseUrl/v1/tags/$name/unfollow").body()

    override suspend fun get(name: String): Tag = client.get("$baseUrl/v1/tags/$name").body()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultTagsServiceFactory : TagsServiceFactory {
    override fun create(@Assisted baseUrl: String, @Assisted client: HttpClient): DefaultTagsService
}
