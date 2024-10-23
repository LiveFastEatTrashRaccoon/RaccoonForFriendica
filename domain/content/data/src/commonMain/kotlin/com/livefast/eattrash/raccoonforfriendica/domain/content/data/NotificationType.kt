package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface NotificationType {
    data object Mention : NotificationType

    data object Reblog : NotificationType

    data object Follow : NotificationType

    data object FollowRequest : NotificationType

    data object Entry : NotificationType

    data object Favorite : NotificationType

    data object Poll : NotificationType

    data object Update : NotificationType

    data object Unknown : NotificationType

    companion object {
        val ALL =
            listOf(
                Entry,
                Favorite,
                Follow,
                FollowRequest,
                Mention,
                Poll,
                Reblog,
                Update,
            )
    }
}

@Composable
fun NotificationType.toReadableName(): String =
    when (this) {
        NotificationType.Entry -> LocalStrings.current.notificationTypeEntryName
        NotificationType.Favorite -> LocalStrings.current.notificationTypeFavoriteName
        NotificationType.Follow -> LocalStrings.current.notificationTypeFollowName
        NotificationType.FollowRequest -> LocalStrings.current.notificationTypeFollowRequestName
        NotificationType.Mention -> LocalStrings.current.notificationTypeMentionName
        NotificationType.Poll -> LocalStrings.current.notificationTypePollName
        NotificationType.Reblog -> LocalStrings.current.notificationTypeReblogName
        NotificationType.Update -> LocalStrings.current.notificationTypeUpdateName
        else -> ""
    }
