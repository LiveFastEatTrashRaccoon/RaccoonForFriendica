package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.PushSubscription
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent

interface PushService {
    suspend fun get(): PushSubscription

    suspend fun create(data: FormDataContent): PushSubscription

    suspend fun update(data: FormDataContent): PushSubscription

    suspend fun delete(): Boolean
}

fun interface PushServiceFactory {
    fun create(baseUrl: String, client: HttpClient): PushService
}
