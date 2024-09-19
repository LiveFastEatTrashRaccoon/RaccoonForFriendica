package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
    @SerialName("accounts") val accounts: Long,
    @SerialName("day") val day: Long,
    @SerialName("uses") val uses: Long,
)
