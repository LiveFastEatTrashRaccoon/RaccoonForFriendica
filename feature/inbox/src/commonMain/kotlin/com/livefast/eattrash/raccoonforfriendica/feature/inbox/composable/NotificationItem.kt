package com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
internal fun NotificationItem(
    notification: NotificationModel,
    modifier: Modifier = Modifier,
    blurNsfw: Boolean = false,
    maxBodyLines: Int = Int.MAX_VALUE,
    autoloadImages: Boolean = true,
    onOpenUrl: ((String) -> Unit)? = null,
    onOpenUser: ((UserModel) -> Unit)? = null,
    onOpenEntry: ((TimelineEntryModel) -> Unit)? = null,
    onUserRelationshipClicked: ((String, RelationshipStatusNextAction) -> Unit)? = null,
) {
    val boxColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val entry = notification.entry
    val user = notification.user
    val contentHorizontalPadding = Spacing.xs

    Column(
        modifier = modifier.semantics(mergeDescendants = true) { },
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = contentHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Icon(
                modifier = Modifier.size(IconSize.s).padding(1.dp),
                imageVector = notification.type.toIcon(),
                contentDescription = null,
                tint = ancillaryColor,
            )
            if (user != null) {
                NotificationHeaderUserInfo(
                    modifier = Modifier.padding(start = Spacing.xs),
                    user = user,
                    autoloadImages = autoloadImages,
                    onOpenUser = {
                        onOpenUser?.invoke(user)
                    },
                )
            }
            Text(
                modifier = Modifier.padding(horizontal = Spacing.xxs).weight(1f),
                text = notification.type.toReadableName(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = ancillaryColor,
            )

            // unread indicator
            if (!notification.read) {
                Box(
                    modifier =
                        Modifier
                            .size(IconSize.xs)
                            .padding(2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape,
                            ),
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .padding(horizontal = contentHorizontalPadding)
                    .background(
                        color = boxColor,
                        shape = RoundedCornerShape(CornerSize.l),
                    ).clip(RoundedCornerShape(CornerSize.l))
                    .padding(
                        start = Spacing.xxs,
                        end = Spacing.xxs,
                        bottom = Spacing.xs,
                    ),
        ) {
            if (entry != null) {
                TimelineItem(
                    modifier =
                        Modifier.padding(
                            top = Spacing.s,
                            bottom = Spacing.s,
                        ),
                    entry = entry,
                    blurNsfw = blurNsfw,
                    maxBodyLines = maxBodyLines,
                    autoloadImages = autoloadImages,
                    actionsEnabled = false,
                    pollEnabled = false,
                    onClick = {
                        onOpenEntry?.invoke(entry)
                    },
                    onOpenUser = onOpenUser,
                    onOpenUrl = onOpenUrl,
                )
            } else if (user != null) {
                NotificationUserInfo(
                    modifier =
                        Modifier.padding(
                            bottom = Spacing.m,
                        ),
                    user = user,
                    autoloadImages = autoloadImages,
                    onOpenUrl = onOpenUrl,
                    onOpenUser = {
                        onOpenUser?.invoke(user)
                    },
                    onRelationshipClicked = { nextAction ->
                        onUserRelationshipClicked?.invoke(user.id, nextAction)
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
        NotificationType.Favorite -> Icons.Default.Favorite
        NotificationType.Follow -> Icons.Default.PersonAdd
        NotificationType.FollowRequest -> Icons.Default.PersonAdd
        NotificationType.Mention -> Icons.Default.ChatBubble
        NotificationType.Poll -> Icons.Default.Poll
        NotificationType.Reblog -> Icons.Default.Loop
        NotificationType.Update -> Icons.Default.Edit
        NotificationType.Unknown -> Icons.Default.QuestionMark
    }
