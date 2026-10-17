package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Page
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ScheduledStatus
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusContext
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusSource
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Translation
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateStatusForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
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

@AssistedInject
class DefaultStatusService(@Assisted private val args: ServiceCreationArgs) : StatusService {

    override suspend fun get(id: String): Status = args.client.get("${args.baseUrl}/v1/statuses/$id").body()

    override suspend fun getContext(id: String): StatusContext =
        args.client.get("${args.baseUrl}/v1/statuses/$id/context").body()

    override suspend fun getSource(id: String): StatusSource =
        args.client.get("${args.baseUrl}/v1/statuses/$id/source").body()

    override suspend fun reblog(id: String, data: FormDataContent): Status =
        args.client.post("${args.baseUrl}/v1/statuses/$id/reblog") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun unreblog(id: String): Status =
        args.client.post("${args.baseUrl}/v1/statuses/$id/unreblog").body()

    override suspend fun pin(id: String): Status = args.client.post("${args.baseUrl}/1/statuses/$id/pin").body()

    override suspend fun unpin(id: String): Status = args.client.post("${args.baseUrl}/v1/statuses/$id/unpin").body()

    override suspend fun bookmark(id: String): Status =
        args.client.post("${args.baseUrl}/v1/statuses/$id/bookmark").body()

    override suspend fun unbookmark(id: String): Status =
        args.client.post("${args.baseUrl}/v1/statuses/$id/unbookmark").body()

    override suspend fun favorite(id: String): Status =
        args.client.post("${args.baseUrl}/v1/statuses/$id/favourite").body()

    override suspend fun unfavorite(id: String): Status =
        args.client.post("${args.baseUrl}/v1/statuses/$id/unfavourite").body()

    override suspend fun getFavoritedBy(id: String, maxId: String?, limit: Int): Page<Account> {
        val response = args.client.get("${args.baseUrl}/v1/statuses/$id/favourited_by") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }
        val data: List<Account> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun getRebloggedBy(id: String, maxId: String?, limit: Int): Page<Account> {
        val response = args.client.get("${args.baseUrl}/v1/statuses/$id/reblogged_by") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }
        val data: List<Account> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun create(key: String, data: CreateStatusForm): Status =
        args.client.post("${args.baseUrl}/v1/statuses") {
            header("Idempotency-Key", key)
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun update(id: String, data: CreateStatusForm): Status =
        args.client.put("${args.baseUrl}/v1/statuses/$id") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun delete(id: String): Boolean =
        args.client.delete("${args.baseUrl}/v1/statuses/$id").status.isSuccess()

    override suspend fun getScheduled(maxId: String?, minId: String?, limit: Int): List<ScheduledStatus> =
        args.client.get("${args.baseUrl}/v1/scheduled_statuses") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }.body()

    override suspend fun getScheduledById(id: String): ScheduledStatus =
        args.client.get("${args.baseUrl}/v1/scheduled_statuses/$id").body()

    override suspend fun updateScheduled(id: String, data: FormDataContent): ScheduledStatus =
        args.client.put("${args.baseUrl}/v1/scheduled_statuses/$id") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun deleteScheduled(id: String): Boolean =
        args.client.delete("${args.baseUrl}/v1/scheduled_statuses/$id").status.isSuccess()

    override suspend fun dislike(data: FormDataContent): Boolean =
        args.client.post("${args.baseUrl}/friendica/activity/dislike") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.status.isSuccess()

    override suspend fun undislike(data: FormDataContent): Boolean =
        args.client.post("${args.baseUrl}/friendica/activity/undislike").status.isSuccess()

    override suspend fun translate(id: String, data: FormDataContent): Translation =
        args.client.post("${args.baseUrl}/v1/statuses/$id/translate").body()

    override suspend fun getQuotes(id: String, maxId: String?, limit: Int): Page<Status> {
        val response = args.client.get("${args.baseUrl}/v1/statuses/$id/quotes") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }
        val data: List<Status> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun revokeQuote(quotedId: String, quotingId: String) =
        args.client.post("${args.baseUrl}/v1/statuses/$quotedId/quotes/$quotingId/revoke").status.isSuccess()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultStatusServiceFactory : StatusServiceFactory {
    override fun create(@Assisted args: ServiceCreationArgs): DefaultStatusService
}
