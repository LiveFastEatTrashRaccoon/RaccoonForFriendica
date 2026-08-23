package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Notification
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.NotificationType
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.serialName
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.isSuccess
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultNotificationService(@InjectedParam args: ServiceCreationArgs) : NotificationService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun get(
        types: List<NotificationType>,
        excludeTypes: List<NotificationType>?,
        maxId: String?,
        minId: String?,
        includeAll: Boolean,
        limit: Int,
    ): Pair<List<Notification>, String?> {
        val response = client.get("$baseUrl/v1/notifications") {
            if (types.isNotEmpty()) {
                types.forEach { value ->
                    parameter("types[]", value.serialName)
                }
            }
            excludeTypes?.forEach { value ->
                parameter("exclude_types[]", value.serialName)
            }
            parameter("max_id", maxId)
            parameter("min_id", minId)

            parameter("include_filtered", includeAll)
            parameter("limit", limit)
        }
        val data: List<Notification> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return data to cursor
    }

    override suspend fun dismiss(id: String): Boolean =
        client.post("$baseUrl/v1/notifications/$id/dismiss").status.isSuccess()

    override suspend fun clear(): Boolean = client.post("$baseUrl/v1/notifications/clear").status.isSuccess()
}
