package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface NotificationStatus {
    data object Enabled : NotificationStatus

    data object Disabled : NotificationStatus
}

fun RelationshipModel.toNotificationStatus(): NotificationStatus? =
    when {
        !following -> null
        notifying -> NotificationStatus.Enabled
        else -> NotificationStatus.Disabled
    }

fun NotificationStatus.toIcon(): ImageVector =
    when (this) {
        NotificationStatus.Disabled -> Icons.Outlined.Notifications
        NotificationStatus.Enabled -> Icons.Default.NotificationsActive
    }
