package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Relationship(
    @SerialName("blocking") val blocking: Boolean = false,
    @SerialName("domain_blocking") val domainBlocking: Boolean = false,
    @SerialName("endorsed") val endorsed: Boolean = false,
    @SerialName("followed_by") val followedBy: Boolean = false,
    @SerialName("following") val following: Boolean = false,
    @SerialName("id") val id: String,
    @SerialName("muting") val muting: Boolean = false,
    @SerialName("muting_notifications") val mutingNotifications: Boolean = false,
    @SerialName("note") val note: String? = null,
    @SerialName("notifying") val notifying: Boolean = false,
    @SerialName("requested") val requested: Boolean = false,
    @SerialName("requested_by") val requestedBy: Boolean = false,
    @SerialName("showing_reblogs") val showingReblogs: Boolean = false,
)
