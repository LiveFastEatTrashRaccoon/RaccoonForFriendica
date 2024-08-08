package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class RelationshipModel(
    val id: String,
    val following: Boolean = false,
    val notifying: Boolean = false,
    val followedBy: Boolean = false,
    val blocking: Boolean = false,
    val muting: Boolean = false,
    val mutingNotifications: Boolean = false,
    val requested: Boolean = false,
    val requestedBy: Boolean = false,
    val note: String? = null,
)
