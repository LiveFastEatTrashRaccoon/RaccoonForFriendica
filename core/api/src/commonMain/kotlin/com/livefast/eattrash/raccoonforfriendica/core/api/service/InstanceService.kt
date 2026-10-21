package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CustomEmoji
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Instance
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.InstanceRule
import io.ktor.client.HttpClient

interface InstanceService {
    suspend fun getInfo(): Instance

    suspend fun getRules(): List<InstanceRule>

    suspend fun getCustomEmojis(): List<CustomEmoji>
}

fun interface InstanceServiceFactory {
    fun create(baseUrl: String, client: HttpClient): InstanceService
}
