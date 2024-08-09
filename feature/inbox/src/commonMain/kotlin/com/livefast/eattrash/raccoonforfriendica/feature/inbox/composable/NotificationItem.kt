package com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

@Composable
fun NotificationItem(
    notification: NotificationModel,
    modifier: Modifier = Modifier,
    onOpenUrl: ((String) -> Unit)? = null,
    onOpenUser: ((AccountModel) -> Unit)? = null,
    onOpenEntry: ((TimelineEntryModel) -> Unit)? = null,
) {
    val boxColor = MaterialTheme.colorScheme.surfaceVariant
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val entry = notification.entry
    val account = notification.account

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Icon(
                modifier = Modifier.size(IconSize.s).padding(1.dp),
                imageVector = notification.type.toIcon(),
                contentDescription = null,
                tint = ancillaryColor,
            )
            if (account != null) {
                NotificationHeaderUserInfo(
                    modifier = Modifier.padding(start = Spacing.xs),
                    account = account,
                    onOpenUser = onOpenUser,
                )
            }
            Text(
                modifier = Modifier.padding(horizontal = Spacing.xxs),
                text = notification.type.toReadableName(),
                style = MaterialTheme.typography.bodyLarge,
                color = ancillaryColor,
            )
        }

        Box(
            modifier =
                Modifier
                    .background(
                        color = boxColor,
                        shape = RoundedCornerShape(CornerSize.l),
                    ).clip(RoundedCornerShape(CornerSize.l)),
        ) {
            if (entry != null) {
                TimelineItem(
                    entry = entry,
                    actionsEnabled = false,
                    onClick = {
                        onOpenEntry?.invoke(entry)
                    },
                    onOpenUser = onOpenUser,
                    onOpenUrl = onOpenUrl,
                )
            } else if (account != null) {
                NotificationUserInfo(
                    modifier = Modifier.padding(bottom = Spacing.m),
                    account = account,
                    onOpenUrl = onOpenUrl,
                    onClick = {
                        onOpenUser?.invoke(account)
                    },
                )
            }
        }
    }
}

@Composable
private fun NotificationType.toReadableName(): String =
    when (this) {
        NotificationType.Entry -> LocalStrings.current.notificationTypeEntry
        NotificationType.Favorite -> LocalStrings.current.notificationTypeFavorite
        NotificationType.Follow -> LocalStrings.current.notificationTypeFollow
        NotificationType.FollowRequest -> LocalStrings.current.notificationTypeFollowRequest
        NotificationType.Mention -> LocalStrings.current.notificationTypeMention
        NotificationType.Poll -> LocalStrings.current.notificationTypePoll
        NotificationType.Reblog -> LocalStrings.current.notificationTypeReblog
        NotificationType.Update -> LocalStrings.current.notificationTypeUpdate
        NotificationType.Unknown -> ""
    }

@Composable
private fun NotificationType.toIcon(): ImageVector =
    when (this) {
        NotificationType.Entry -> Icons.Default.PostAdd
        NotificationType.Favorite -> Icons.Default.Star
        NotificationType.Follow -> Icons.Default.PersonAdd
        NotificationType.FollowRequest -> Icons.Default.PersonAdd
        NotificationType.Mention -> Icons.Default.ChatBubble
        NotificationType.Poll -> Icons.Default.Poll
        NotificationType.Reblog -> Icons.Default.Loop
        NotificationType.Update -> Icons.Default.Edit
        NotificationType.Unknown -> Icons.Default.QuestionMark
    }
