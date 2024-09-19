package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class RelationshipModel(
    val blocking: Boolean = false,
    val followedBy: Boolean = false,
    val following: Boolean = false,
    val id: String,
    val muting: Boolean = false,
    val mutingNotifications: Boolean = false,
    val note: String? = null,
    val notifying: Boolean = false,
    val requested: Boolean = false,
    val requestedBy: Boolean = false,
)
