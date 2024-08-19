package com.livefast.eattrash.raccoonforfriendica.core.api.form

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MuteUserForm(
    @SerialName("notifications") val notifications: Boolean = true,
    @SerialName("duration") val duration: Long = 0,
)
