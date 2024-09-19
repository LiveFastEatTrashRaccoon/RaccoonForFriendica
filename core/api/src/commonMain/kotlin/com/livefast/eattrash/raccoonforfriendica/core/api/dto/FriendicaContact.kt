package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendicaContact(
    @SerialName("description") val description: String? = null,
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
    @SerialName("screen_name") val screenName: String? = null,
    @SerialName("url") val url: String? = null,
)
