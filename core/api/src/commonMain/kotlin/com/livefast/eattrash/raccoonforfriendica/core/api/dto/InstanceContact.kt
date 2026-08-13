package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceContact(
    @SerialName("email") val email: String? = null,
    @SerialName("account") val account: Account? = null,
)
