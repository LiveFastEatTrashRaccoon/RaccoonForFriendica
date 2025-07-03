package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Application
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateAppForm
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class DefaultAppService(private val baseUrl: String, private val client: HttpClient) : AppService {
    override suspend fun create(data: CreateAppForm): Application = client.post("$baseUrl/v1/apps") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    override suspend fun verifyCredentials(): Application = client.get("$baseUrl/v1/apps/verify_credentials").body()
}
