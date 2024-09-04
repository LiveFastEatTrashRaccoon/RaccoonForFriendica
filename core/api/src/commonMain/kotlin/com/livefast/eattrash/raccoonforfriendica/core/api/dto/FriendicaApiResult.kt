package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendicaApiResult(
    @SerialName("result") val result: String,
    @SerialName("message") val message: String? = null,
)
