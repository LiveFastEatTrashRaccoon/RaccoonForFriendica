package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CustomEmoji
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Instance
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.InstanceRule
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.get

@AssistedInject
class DefaultInstanceService(@Assisted args: ServiceCreationArgs) : InstanceService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun getInfo(): Instance = client.get("$baseUrl/v2/instance").body()

    override suspend fun getRules(): List<InstanceRule> = client.get("$baseUrl/v1/instance/rules").body()

    override suspend fun getCustomEmojis(): List<CustomEmoji> = client.get("$baseUrl/v1/custom_emojis").body()
}

@AssistedFactory
fun interface InstanceServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultInstanceService
}
