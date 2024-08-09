package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class NotificationModel(
    val id: String,
    val type: NotificationType = NotificationType.Unknown,
    val user: UserModel? = null,
    val entry: TimelineEntryModel? = null,
)
