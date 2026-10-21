package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

@AssistedInject
class DefaultTrendsService(@Assisted private val baseUrl: String, @Assisted private val client: HttpClient) :
    TrendsService {

    override suspend fun getHashtags(offset: Int, limit: Int): List<Tag> = client.get("$baseUrl/v1/trends/tags") {
        parameter("offset", offset)
        parameter("limit", limit)
    }.body()

    override suspend fun getStatuses(offset: Int, limit: Int): List<Status> =
        client.get("$baseUrl/v1/trends/statuses") {
            parameter("offset", offset)
            parameter("limit", limit)
        }.body()

    override suspend fun getLinks(offset: Int, limit: Int): List<TrendsLink> = client.get("$baseUrl/v1/trends/links") {
        parameter("offset", offset)
        parameter("limit", limit)
    }.body()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultTrendsServiceFactory : TrendsServiceFactory {
    override fun create(@Assisted baseUrl: String, @Assisted client: HttpClient): DefaultTrendsService
}
