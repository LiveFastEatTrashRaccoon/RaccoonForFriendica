package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface NotificationStatus {
    data object Enabled : NotificationStatus

    data object Disabled : NotificationStatus

    data object Undetermined : NotificationStatus
}

fun RelationshipModel.toNotificationStatus(): NotificationStatus =
    when {
        mutingNotifications -> NotificationStatus.Disabled
        notifying -> NotificationStatus.Enabled
        else -> NotificationStatus.Undetermined
    }
