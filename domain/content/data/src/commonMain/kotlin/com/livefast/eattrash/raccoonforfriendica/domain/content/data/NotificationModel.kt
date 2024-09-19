package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class NotificationModel(
    val entry: TimelineEntryModel? = null,
    val id: String,
    val read: Boolean = true,
    val type: NotificationType = NotificationType.Unknown,
    val user: UserModel? = null,
)
