package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendicaPhotoAlbum(
    @SerialName("name") val name: String,
    @SerialName("created") val created: String? = null,
    @SerialName("count") val count: Int = 0,
)
