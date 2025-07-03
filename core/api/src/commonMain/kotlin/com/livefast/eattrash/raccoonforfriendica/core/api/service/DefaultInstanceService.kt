package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CustomEmoji
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Instance
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.InstanceRule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class DefaultInstanceService(private val baseUrl: String, private val client: HttpClient) : InstanceService {
    override suspend fun getInfo(): Instance = client.get("$baseUrl/v2/instance").body()

    override suspend fun getRules(): List<InstanceRule> = client.get("$baseUrl/v1/instance/rules").body()

    override suspend fun getCustomEmojis(): List<CustomEmoji> = client.get("$baseUrl/v1/custom_emojis").body()
}
