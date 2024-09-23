package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @SerialName("acct") val acct: String,
    @SerialName("avatar") val avatar: String? = null,
    @SerialName("bot") val bot: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("discoverable") val discoverable: Boolean? = null,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("emojis") val emojis: List<CustomEmoji>? = null,
    @SerialName("fields") val fields: List<Field> = emptyList(),
    @SerialName("followers_count") val followersCount: Int = 0,
    @SerialName("following_count") val followingCount: Int = 0,
    @SerialName("group") val group: Boolean = false,
    @SerialName("header") val header: String? = null,
    @SerialName("id") val id: String,
    @SerialName("locked") val locked: Boolean = false,
    @SerialName("noindex") val noIndex: Boolean = false,
    @SerialName("note") val note: String? = null,
    @SerialName("statuses_count") val statusesCount: Int = 0,
    @SerialName("url") val url: String? = null,
    @SerialName("username") val username: String,
)
