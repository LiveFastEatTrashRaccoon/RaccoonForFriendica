package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

internal class DefaultTrendService(private val baseUrl: String, private val client: HttpClient) : TrendsService {
    override suspend fun getHashtags(offset: Int, limit: Int): List<Tag> = client.get("$baseUrl/v1/trends/tags") {
        parameter("offset", offset)
        parameter("limit", limit)
    }.body()

    override suspend fun getStatuses(offset: Int, limit: Int): List<Status> =
        client.get("$baseUrl/v1/trends/statuses") {
            parameter("offset", offset)
            parameter("limit", limit)
        }.body()

    override suspend fun getLinks(offset: Int, limit: Int): HttpResponse = client.get("$baseUrl/v1/trends/links") {
        parameter("offset", offset)
        parameter("limit", limit)
    }
}
