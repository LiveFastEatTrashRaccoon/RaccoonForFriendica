package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceUsageUsers(
    @SerialName("active_month") val activeMonth: Int? = null,
)
