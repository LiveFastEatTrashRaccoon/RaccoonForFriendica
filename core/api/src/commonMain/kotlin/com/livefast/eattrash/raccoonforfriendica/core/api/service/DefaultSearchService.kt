package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Search
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

@AssistedInject
class DefaultSearchService(@Assisted private val args: ServiceCreationArgs) : SearchService {

    override suspend fun search(
        query: String,
        type: String,
        offset: Int?,
        following: Boolean,
        limit: Int,
        resolve: Boolean,
    ): Search = args.client.get("${args.baseUrl}/v2/search") {
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
    override fun create(@Assisted args: ServiceCreationArgs): DefaultSearchService
}
