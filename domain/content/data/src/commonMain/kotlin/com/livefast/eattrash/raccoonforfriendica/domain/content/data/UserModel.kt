package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.Transient

data class UserModel(
    val avatar: String? = null,
    val bio: String? = null,
    @Transient val blocked: Boolean = false,
    val bot: Boolean = false,
    val created: String? = null,
    val discoverable: Boolean = true,
    val displayName: String? = null,
    val entryCount: Int = 0,
    val fields: List<FieldModel> = emptyList(),
    val followers: Int = 0,
    val following: Int = 0,
    val group: Boolean = false,
    val handle: String? = null,
    val header: String? = null,
    val hideCollections: Boolean = false,
    val id: String,
    val locked: Boolean = false,
    @Transient val muted: Boolean = false,
    val noIndex: Boolean = false,
    @Transient val notificationStatus: NotificationStatus? = null,
    @Transient val notificationStatusPending: Boolean = false,
    @Transient val relationshipStatus: RelationshipStatus? = null,
    @Transient val relationshipStatusPending: Boolean = false,
    val url: String? = null,
    val username: String? = null,
)
