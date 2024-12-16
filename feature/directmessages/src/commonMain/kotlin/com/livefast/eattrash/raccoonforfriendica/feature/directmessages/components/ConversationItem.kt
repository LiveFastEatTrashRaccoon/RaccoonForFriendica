package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentBody
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentTitle
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TextWithCustomEmojis
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ConversationModel

@Composable
internal fun ConversationItem(
    conversation: ConversationModel,
    modifier: Modifier = Modifier,
    autoloadImages: Boolean = true,
    onClick: ((String) -> Unit)? = null,
) {
    val user = conversation.otherUser
    val avatar = user.avatar.orEmpty()
    val message = conversation.lastMessage
    val avatarSize = IconSize.l
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val parentUri = message.parentUri.orEmpty()

    Column(
        modifier =
            modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onClick?.invoke(parentUri)
            },
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Row(
            modifier = Modifier.padding(Spacing.s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            if (avatar.isNotEmpty() && autoloadImages) {
                CustomImage(
                    modifier =
                        Modifier
                            .size(avatarSize)
                            .clip(RoundedCornerShape(avatarSize / 2)),
                    url = avatar,
                    quality = FilterQuality.Low,
                    contentScale = ContentScale.FillBounds,
                )
            } else {
                PlaceholderImage(
                    size = avatarSize,
                    title = user.displayName ?: user.handle ?: "?",
                )
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                TextWithCustomEmojis(
                    text = user.displayName ?: user.username ?: "",
                    emojis = user.emojis,
                    style = MaterialTheme.typography.titleMedium,
                    autoloadImages = autoloadImages,
                    color = fullColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = user.handle ?: user.username ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = ancillaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        val contentPadding = 10.dp
        message.title?.also { title ->
            ContentTitle(
                modifier = Modifier.padding(horizontal = contentPadding),
                content = title,
                onClick = {
                    onClick?.invoke(parentUri)
                },
            )
        }

        message.text?.also { text ->
            ContentBody(
                modifier = Modifier.padding(horizontal = contentPadding),
                content = text,
                onClick = {
                    onClick?.invoke(parentUri)
                },
            )
        }

        Text(
            modifier =
                Modifier.padding(
                    top = Spacing.xs,
                    start = contentPadding,
                    end = contentPadding,
                    bottom = Spacing.xs,
                ),
            text =
                buildAnnotatedString {
                    append(LocalStrings.current.messages(conversation.messageCount))

                    if (conversation.unreadCount > 0) {
                        append(" (")
                        withStyle(SpanStyle(color = fullColor)) {
                            append(LocalStrings.current.unreadMessages(conversation.unreadCount))
                        }
                        append(")")
                    }

                    val date = message.created
                    if (!date.isNullOrEmpty()) {
                        append(" • ")
                        append(date.prettifyDate())
                    }
                },
            style = MaterialTheme.typography.titleSmall,
            color = ancillaryColor,
        )
    }
}
