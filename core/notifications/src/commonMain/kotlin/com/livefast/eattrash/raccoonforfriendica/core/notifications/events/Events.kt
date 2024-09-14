package com.livefast.eattrash.raccoonforfriendica.core.notifications.events

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

data class UserUpdatedEvent(
    val user: UserModel,
) : NotificationCenterEvent

data class TimelineEntryUpdatedEvent(
    val entry: TimelineEntryModel,
) : NotificationCenterEvent

data class TagUpdatedEvent(
    val tag: TagModel,
) : NotificationCenterEvent

data object AlbumsUpdatedEvent : NotificationCenterEvent

data class DraftDeletedEvent(
    val id: String,
) : NotificationCenterEvent
