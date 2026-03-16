package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources

sealed interface NotificationStatus {
    data object Enabled : NotificationStatus

    data object Disabled : NotificationStatus
}

fun RelationshipModel.toNotificationStatus(): NotificationStatus? = when {
    !following -> null
    notifying -> NotificationStatus.Enabled
    else -> NotificationStatus.Disabled
}

@Composable
fun NotificationStatus.toIcon(coreResources: CoreResources): ImageVector = when (this) {
    NotificationStatus.Disabled -> coreResources.notifications
    NotificationStatus.Enabled -> coreResources.notificationsActive
}
