package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    @SerialName("id") val id: Long,
    @SerialName("uid") val uid: Long,
    @SerialName("cid") val cid: Long,
    @SerialName("uri") val uri: String,
    @SerialName("name") val name: String,
    @SerialName("desc") val description: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String? = null,
    @SerialName("type") val type: String,
    @SerialName("nofinish") val noFinish: Int = 0,
    @SerialName("place") val place: String? = null,
)
