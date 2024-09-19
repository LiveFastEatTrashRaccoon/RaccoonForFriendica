package com.livefast.eattrash.raccoonforfriendica.core.api.form

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MuteUserForm(
    @SerialName("duration") val duration: Long = 0,
    @SerialName("notifications") val notifications: Boolean = true,
)
