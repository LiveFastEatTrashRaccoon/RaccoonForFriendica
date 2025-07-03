package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Search
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class DefaultSearchService(private val baseUrl: String, private val client: HttpClient) : SearchService {
    override suspend fun search(
        query: String,
        type: String,
        maxId: String?,
        minId: String?,
        following: Boolean,
        limit: Int,
        resolve: Boolean,
    ): Search = client.get("$baseUrl/v2/search") {
        parameter("q", query)
        parameter("type", type)
        parameter("max_id", maxId)
        parameter("min_id", minId)
        parameter("following", following)
        parameter("limit", limit)
        parameter("resolve", resolve)
    }.body()
}
