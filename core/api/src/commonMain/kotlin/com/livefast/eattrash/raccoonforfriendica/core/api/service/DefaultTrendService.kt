package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
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
class DefaultTrendsService(@Assisted private val args: ServiceCreationArgs) : TrendsService {

    override suspend fun getHashtags(offset: Int, limit: Int): List<Tag> =
        args.client.get("${args.baseUrl}/v1/trends/tags") {
            parameter("offset", offset)
            parameter("limit", limit)
        }.body()

    override suspend fun getStatuses(offset: Int, limit: Int): List<Status> =
        args.client.get("${args.baseUrl}/v1/trends/statuses") {
            parameter("offset", offset)
            parameter("limit", limit)
        }.body()

    override suspend fun getLinks(offset: Int, limit: Int): List<TrendsLink> =
        args.client.get("${args.baseUrl}/v1/trends/links") {
            parameter("offset", offset)
            parameter("limit", limit)
        }.body()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultTrendsServiceFactory : TrendsServiceFactory {
    override fun create(@Assisted args: ServiceCreationArgs): DefaultTrendsService
}
