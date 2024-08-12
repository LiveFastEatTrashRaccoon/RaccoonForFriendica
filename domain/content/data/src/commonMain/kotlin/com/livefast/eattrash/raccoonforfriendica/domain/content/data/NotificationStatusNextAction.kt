package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface NotificationStatusNextAction {
    data object Enable : NotificationStatusNextAction

    data object Disable : NotificationStatusNextAction
}

fun NotificationStatus.getNextAction(): NotificationStatusNextAction =
    when (this) {
        NotificationStatus.Disabled -> NotificationStatusNextAction.Enable
        NotificationStatus.Enabled -> NotificationStatusNextAction.Disable
    }
