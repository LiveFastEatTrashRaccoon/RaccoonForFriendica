package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendicaApiResult(
    @SerialName("message") val message: String? = null,
    @SerialName("result") val result: String,
)
