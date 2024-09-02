package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceUsage(
    @SerialName("users") val users: InstanceUsageUsers? = null,
)
