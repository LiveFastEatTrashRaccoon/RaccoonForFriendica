package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Search
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultSearchService(@InjectedParam args: ServiceCreationArgs) : SearchService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun search(
        query: String,
        type: String,
        offset: Int?,
        following: Boolean,
        limit: Int,
        resolve: Boolean,
    ): Search = client.get("$baseUrl/v2/search") {
        parameter("q", query)
        parameter("type", type)
        parameter("offset", offset)
        parameter("following", following)
        parameter("limit", limit)
        parameter("resolve", resolve)
    }.body()
}
