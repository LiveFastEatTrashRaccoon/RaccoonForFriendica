package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.PushSubscription
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

@AssistedInject
class DefaultPushService(@Assisted args: ServiceCreationArgs) : PushService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun get(): PushSubscription = client.get("$baseUrl/v1/push/subscription").body()

    override suspend fun create(data: FormDataContent): PushSubscription =
        client.post("$baseUrl/v1/push/subscription") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun update(data: FormDataContent): PushSubscription = client.put("$baseUrl/v1/push/subscription") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    override suspend fun delete() = client.post("$baseUrl/").status.isSuccess()
}

@AssistedFactory
fun interface PushServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultPushService
}
