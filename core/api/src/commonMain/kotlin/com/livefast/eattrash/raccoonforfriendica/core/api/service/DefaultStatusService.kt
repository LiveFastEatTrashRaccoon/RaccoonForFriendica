package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ScheduledStatus
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusContext
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusSource
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Translation
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateStatusForm
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

internal class DefaultStatusService(private val baseUrl: String, private val client: HttpClient) : StatusService {
    override suspend fun get(id: String): Status = client.get("$baseUrl/v1/statuses/$id").body()

    override suspend fun getContext(id: String): StatusContext = client.get("$baseUrl/v1/statuses/$id/context").body()

    override suspend fun getSource(id: String): StatusSource = client.get("$baseUrl/v1/statuses/$id/source").body()

    override suspend fun reblog(id: String, data: FormDataContent): Status =
        client.post("$baseUrl/v1/statuses/$id/reblog") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun unreblog(id: String): Status = client.post("$baseUrl/v1/statuses/$id/unreblog").body()

    override suspend fun pin(id: String): Status = client.post("$baseUrl/1/statuses/$id/pin").body()

    override suspend fun unpin(id: String): Status = client.post("$baseUrl/v1/statuses/$id/unpin").body()

    override suspend fun bookmark(id: String): Status = client.post("$baseUrl/v1/statuses/$id/bookmark").body()

    override suspend fun unbookmark(id: String): Status = client.post("$baseUrl/v1/statuses/$id/unbookmark").body()

    override suspend fun favorite(id: String): Status = client.post("$baseUrl/v1/statuses/$id/favourite").body()

    override suspend fun unfavorite(id: String): Status = client.post("$baseUrl/v1/statuses/$id/unfavourite").body()

    override suspend fun getFavoritedBy(id: String, maxId: String?, limit: Int): List<Account> =
        client.get("$baseUrl/v1/statuses/$id/favourited_by") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }.body()

    override suspend fun getRebloggedBy(id: String, maxId: String?, limit: Int): List<Account> =
        client.get("$baseUrl/v1/statuses/$id/reblogged_by") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }.body()

    override suspend fun create(key: String, data: CreateStatusForm): Status = client.post("$baseUrl/v1/statuses") {
        header("Idempotency-Key", key)
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    override suspend fun update(id: String, data: CreateStatusForm): Status = client.put("$baseUrl/v1/statuses/$id") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    override suspend fun delete(id: String): Boolean = client.delete("$baseUrl/v1/statuses/$id").status.isSuccess()

    override suspend fun getScheduled(maxId: String?, minId: String?, limit: Int): List<ScheduledStatus> =
        client.get("$baseUrl/v1/scheduled_statuses") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }.body()

    override suspend fun getScheduledById(id: String): ScheduledStatus =
        client.get("$baseUrl/v1/scheduled_statuses/$id").body()

    override suspend fun updateScheduled(id: String, data: FormDataContent): ScheduledStatus =
        client.put("$baseUrl/v1/scheduled_statuses/$id") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun deleteScheduled(id: String): Boolean =
        client.delete("$baseUrl/v1/scheduled_statuses/$id").status.isSuccess()

    override suspend fun dislike(data: FormDataContent): Boolean = client.post("$baseUrl/friendica/activity/dislike") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.status.isSuccess()

    override suspend fun undislike(data: FormDataContent): Boolean =
        client.post("$baseUrl/friendica/activity/undislike").status.isSuccess()

    override suspend fun translate(id: String, data: FormDataContent): Translation =
        client.post("$baseUrl/v1/statuses/$id/translate").body()
}
