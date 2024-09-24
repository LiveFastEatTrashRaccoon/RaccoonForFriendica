package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CustomEmoji
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Instance
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.InstanceRule
import de.jensklingenberg.ktorfit.http.GET

interface InstanceService {
    @GET("v1/instance")
    suspend fun getInfo(): Instance

    @GET("v1/instance/rules")
    suspend fun getRules(): List<InstanceRule>

    @GET("v1/custom_emojis")
    suspend fun getCustomEmojis(): List<CustomEmoji>
}
