package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

@AssistedInject
class DefaultTrendsService(@Assisted args: ServiceCreationArgs) : TrendsService {
    private val baseUrl = args.baseUrl
    private val client = args.client

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

@AssistedFactory
fun interface TrendsServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultTrendsService
}
