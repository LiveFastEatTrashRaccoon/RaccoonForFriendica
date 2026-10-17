package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPrivateMessage
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

@AssistedInject
class DefaultDirectMessageService(@Assisted private val args: ServiceCreationArgs) : DirectMessageService {

    override suspend fun getAll(count: Int, page: Int, maxId: Long?, getText: String): List<FriendicaPrivateMessage> =
        args.client.get("${args.baseUrl}/direct_messages/all") {
            parameter("count", count)
            parameter("page", page)
            parameter("max_id", maxId)
            parameter("getText", getText)
        }.body()

    override suspend fun getConversation(
        uri: String,
        count: Int,
        page: Int,
        maxId: Long?,
        sinceId: Long?,
        getText: String,
    ): List<FriendicaPrivateMessage> = args.client.get("${args.baseUrl}/direct_messages/conversation") {
        parameter("uri", uri)
        parameter("count", count)
        parameter("page", page)
        parameter("max_id", maxId)
        parameter("since_id", sinceId)
        parameter("getText", getText)
    }.body()

    override suspend fun create(data: FormDataContent): FriendicaPrivateMessage =
        args.client.post("${args.baseUrl}/direct_messages/new") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun delete(id: Long): FriendicaApiResult =
        args.client.post("${args.baseUrl}/direct_messages/destroy") {
            parameter("id", id)
        }.body()

    override suspend fun markAsRead(id: Long): FriendicaApiResult =
        args.client.post("${args.baseUrl}/friendica/direct_messages_setseen") {
            parameter("id", id)
        }.body()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultDirectMessageServiceFactory : DirectMessageServiceFactory {
    override fun create(@Assisted args: ServiceCreationArgs): DefaultDirectMessageService
}
