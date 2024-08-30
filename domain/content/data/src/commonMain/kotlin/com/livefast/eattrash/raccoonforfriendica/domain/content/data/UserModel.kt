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
    val discoverable: Boolean = true,
    val hideCollections: Boolean = false,
    val noIndex: Boolean = false,
    val username: String? = null,
    @Transient
    val relationshipStatus: RelationshipStatus? = null,
    @Transient
    val notificationStatus: NotificationStatus? = null,
    @Transient
    val muted: Boolean = false,
    @Transient
    val blocked: Boolean = false,
    @Transient
    val relationshipStatusPending: Boolean = false,
    @Transient
    val notificationStatusPending: Boolean = false,
)
