package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.Transient

data class UserModel(
    val avatar: String? = null,
    val bio: String? = null,
    val bot: Boolean = false,
    val created: String? = null,
    val displayName: String? = null,
    val entryCount: Int = 0,
    val fields: List<FieldModel> = emptyList(),
    val followers: Int = 0,
    val following: Int = 0,
    val group: Boolean = false,
    val handle: String? = null,
    val header: String? = null,
    val id: String,
    val locked: Boolean = false,
    val username: String? = null,
    val relationshipStatus: RelationshipStatus? = null,
    val notificationStatus: NotificationStatus? = null,
    @Transient
    val relationshipStatusPending: Boolean = false,
    @Transient
    val notificationStatusPending: Boolean = false,
)
