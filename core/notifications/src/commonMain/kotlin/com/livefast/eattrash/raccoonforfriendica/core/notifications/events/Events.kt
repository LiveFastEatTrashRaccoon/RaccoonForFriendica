package com.livefast.eattrash.raccoonforfriendica.core.notifications.events

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

data class UserUpdatedEvent(
    val user: UserModel,
) : NotificationCenterEvent
