package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultTrendService(@InjectedParam args: ServiceCreationArgs) : TrendsService {
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
