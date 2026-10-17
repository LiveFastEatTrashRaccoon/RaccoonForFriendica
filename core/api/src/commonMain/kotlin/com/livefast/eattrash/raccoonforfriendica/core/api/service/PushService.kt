package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.PushSubscription
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import io.ktor.client.request.forms.FormDataContent

interface PushService {
    suspend fun get(): PushSubscription

    suspend fun create(data: FormDataContent): PushSubscription

    suspend fun update(data: FormDataContent): PushSubscription

    suspend fun delete(): Boolean
}

fun interface PushServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): PushService
}
