package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Application
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateAppForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultAppService(@InjectedParam args: ServiceCreationArgs) : AppService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun create(data: CreateAppForm): Application = client.post("$baseUrl/v1/apps") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    override suspend fun verifyCredentials(): Application = client.get("$baseUrl/v1/apps/verify_credentials").body()
}
