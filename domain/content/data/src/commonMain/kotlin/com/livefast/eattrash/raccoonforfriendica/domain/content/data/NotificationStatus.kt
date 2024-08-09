package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.ui.graphics.vector.ImageVector

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

fun NotificationStatus.toIcon(): ImageVector =
    when (this) {
        NotificationStatus.Disabled -> Icons.Default.NotificationsNone
        NotificationStatus.Enabled -> Icons.Default.NotificationsActive
        NotificationStatus.Undetermined -> Icons.Default.Notifications
    }
