package com.livefast.eattrash.raccoonforfriendica.core.api.form

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePollForm(
    @SerialName("options") val options: List<String> = emptyList(),
    @SerialName("expires_in") val expiresIn: Long = 0,
    @SerialName("multiple") val multiple: Boolean = false,
)
