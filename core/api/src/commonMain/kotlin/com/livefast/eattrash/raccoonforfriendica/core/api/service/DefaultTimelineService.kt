package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultTimelineService(@InjectedParam args: ServiceCreationArgs) : TimelineService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun getPublic(maxId: String?, minId: String?, limit: Int, local: Boolean): List<Status> =
        client.get("$baseUrl/v1/timelines/public") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
            parameter("local", local)
        }.body()

    override suspend fun getHome(maxId: String?, minId: String?, limit: Int): List<Status> =
        client.get("$baseUrl/v1/timelines/home") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }.body()

    override suspend fun getHashtag(
        hashtag: String,
        maxId: String?,
        minId: String?,
        limit: Int,
    ): Pair<List<Status>, String?> {
        val response = client.get("$baseUrl/v1/timelines/tag/$hashtag") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }
        val data: List<Status> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return data to cursor
    }

    override suspend fun getList(id: String, maxId: String?, minId: String?, limit: Int): List<Status> =
        client.get("$baseUrl/v1/timelines/list/$id") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }.body()
}
