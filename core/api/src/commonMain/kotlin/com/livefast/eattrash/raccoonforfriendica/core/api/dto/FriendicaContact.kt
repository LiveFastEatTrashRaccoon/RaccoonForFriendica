package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendicaContact(
    @SerialName("id_str") val id: String,
    @SerialName("name") val username: String,
    @SerialName("screen_name") val displayName: String,
    @SerialName("location") val location: String,
    @SerialName("url") val url: String,
    @SerialName("description") val note: String,
    @SerialName("profile_image_url") val avatar: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("followers_count") val followersCount: Int = 0,
    @SerialName("protected") val locked: Boolean = false,
    @SerialName("statuses_count") val statusesCount: Int = 0,
)
