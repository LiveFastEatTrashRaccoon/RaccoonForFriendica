package com.livefast.eattrash.raccoonforfriendica.core.notifications.events

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

data class UserUpdatedEvent(val user: UserModel) : NotificationCenterEvent

data class PostCreatedEvent(val post: PostModel) : NotificationCenterEvent

data class PostUpdatedEvent(val post: PostModel) : NotificationCenterEvent

data class PostDeletedEvent(val id: String) : NotificationCenterEvent

data class TagUpdatedEvent(val tag: TagModel) : NotificationCenterEvent

data object AlbumsUpdatedEvent : NotificationCenterEvent

data class DraftDeletedEvent(val id: String) : NotificationCenterEvent
