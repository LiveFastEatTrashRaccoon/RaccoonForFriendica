package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Notification
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.isSuccess

internal class DefaultNotificationService(private val baseUrl: String, private val client: HttpClient) :
    NotificationService {
    override suspend fun get(
        types: List<String>,
        excludeTypes: List<String>?,
        maxId: String?,
        minId: String?,
        includeAll: Boolean,
        limit: Int,
    ): List<Notification> = client.get("$baseUrl/v1/notifications") {
        types.forEach { value ->
            parameter("types[]", value)
        }
        excludeTypes?.forEach { value ->
            parameter("exclude_types[]", value)
        }
        parameter("max_id", maxId)
        parameter("minId", minId)
        parameter("include_all", includeAll)
        parameter("limit", limit)
    }.body()

    override suspend fun dismiss(id: String): Boolean =
        client.post("$baseUrl/v1/notifications/$id/dismiss").status.isSuccess()

    override suspend fun clear(): Boolean = client.post("$baseUrl/v1/notifications/clear").status.isSuccess()
}
