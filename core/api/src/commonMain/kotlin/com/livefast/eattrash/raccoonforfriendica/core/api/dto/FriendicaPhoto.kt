package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendicaPhoto(
    @SerialName("id") val id: String,
    @SerialName("desc") val desc: String? = null,
    @SerialName("album") val album: String? = null,
    @SerialName("filename") val filename: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("link") val link: List<String> = emptyList(),
    @SerialName("media-id") val mediaId: String? = null,
    @SerialName("thumb") val thumb: String? = null,
)
