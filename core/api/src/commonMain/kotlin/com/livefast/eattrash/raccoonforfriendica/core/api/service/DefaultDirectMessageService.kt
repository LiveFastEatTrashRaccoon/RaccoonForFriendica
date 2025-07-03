package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPrivateMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class DefaultDirectMessageService(private val baseUrl: String, private val client: HttpClient) :
    DirectMessageService {
    override suspend fun getAll(count: Int, page: Int, maxId: Long?, getText: String): List<FriendicaPrivateMessage> =
        client.get("$baseUrl/direct_messages/all") {
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
    ): List<FriendicaPrivateMessage> = client.get("$baseUrl/direct_messages/conversation") {
        parameter("uri", uri)
        parameter("count", count)
        parameter("page", page)
        parameter("max_id", maxId)
        parameter("since_id", sinceId)
        parameter("getText", getText)
    }.body()

    override suspend fun create(data: FormDataContent): FriendicaPrivateMessage =
        client.post("$baseUrl/direct_messages/new") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun delete(id: Long): FriendicaApiResult = client.post("$baseUrl/direct_messages/destroy") {
        parameter("id", id)
    }.body()

    override suspend fun markAsRead(id: Long): FriendicaApiResult =
        client.post("$baseUrl/friendica/direct_messages_setseen") {
            parameter("id", id)
        }.body()
}
