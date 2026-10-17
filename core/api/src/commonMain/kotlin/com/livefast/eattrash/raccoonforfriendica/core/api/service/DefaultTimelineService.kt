package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Page
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

@AssistedInject
class DefaultTimelineService(@Assisted private val args: ServiceCreationArgs) : TimelineService {

    override suspend fun getPublic(maxId: String?, minId: String?, limit: Int, local: Boolean): List<Status> =
        args.client.get("${args.baseUrl}/v1/timelines/public") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
            parameter("local", local)
        }.body()

    override suspend fun getHome(maxId: String?, minId: String?, limit: Int): List<Status> =
        args.client.get("${args.baseUrl}/v1/timelines/home") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }.body()

    override suspend fun getHashtag(hashtag: String, maxId: String?, minId: String?, limit: Int): Page<Status> {
        val response = args.client.get("${args.baseUrl}/v1/timelines/tag/$hashtag") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }
        val data: List<Status> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun getList(id: String, maxId: String?, minId: String?, limit: Int): List<Status> =
        args.client.get("${args.baseUrl}/v1/timelines/list/$id") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }.body()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultTimelineServiceFactory : TimelineServiceFactory {
    override fun create(@Assisted args: ServiceCreationArgs): DefaultTimelineService
}
