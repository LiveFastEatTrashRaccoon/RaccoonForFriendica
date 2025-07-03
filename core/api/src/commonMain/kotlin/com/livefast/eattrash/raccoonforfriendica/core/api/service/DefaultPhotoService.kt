package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class DefaultPhotoService(private val baseUrl: String, private val client: HttpClient) : PhotoService {
    override suspend fun create(content: MultiPartFormDataContent): FriendicaPhoto =
        client.post("$baseUrl/friendica/photo/create") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()

    override suspend fun update(content: MultiPartFormDataContent): FriendicaApiResult =
        client.post("$baseUrl/friendica/photo/update") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()

    override suspend fun delete(content: MultiPartFormDataContent): FriendicaApiResult =
        client.post("$baseUrl/friendica/photo/delete") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()
}
