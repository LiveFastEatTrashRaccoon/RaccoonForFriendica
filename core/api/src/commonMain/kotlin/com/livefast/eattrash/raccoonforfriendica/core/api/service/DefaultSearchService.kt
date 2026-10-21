package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Search
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
class DefaultSearchService(@Assisted private val baseUrl: String, @Assisted private val client: HttpClient) :
    SearchService {

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

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultSearchServiceFactory : SearchServiceFactory {
    override fun create(@Assisted baseUrl: String, @Assisted client: HttpClient): DefaultSearchService
}
