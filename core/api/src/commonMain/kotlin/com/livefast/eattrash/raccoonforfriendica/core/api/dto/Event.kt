package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    @SerialName("cid") val cid: Long? = null,
    @SerialName("desc") val description: String,
    @SerialName("end_time") val endTime: String? = null,
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("nofinish") val noFinish: Int = 0,
    @SerialName("place") val place: String? = null,
    @SerialName("start_time") val startTime: String,
    @SerialName("type") val type: String? = null,
    @SerialName("uid") val uid: Long? = 0,
    @SerialName("uri") val uri: String,
)
