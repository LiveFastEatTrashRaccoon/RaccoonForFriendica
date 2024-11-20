package com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentBody
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TextWithCustomEmojis
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserRelationshipButton
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
fun NotificationUserInfo(
    user: UserModel,
    modifier: Modifier = Modifier,
    autoloadImages: Boolean = true,
    onOpenUser: (() -> Unit)? = null,
    onOpenUrl: ((String) -> Unit)? = null,
    onRelationshipClicked: ((RelationshipStatusNextAction) -> Unit)? = null,
) {
    val banner = user.header.orEmpty()
    val avatar = user.avatar.orEmpty()
    val avatarSize = 60.dp
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val relationshipStatus = user.relationshipStatus
    val onOpenUserModifier =
        if (onOpenUser != null) {
            Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onOpenUser.invoke()
            }
        } else {
            Modifier
        }

    Column(
        modifier = modifier.then(onOpenUserModifier),
    ) {
        Box {
            CustomImage(
                modifier =
                    Modifier
                        .padding(bottom = avatarSize * 0.8f)
                        .aspectRatio(16 / 5f),
                url = banner,
                autoload = autoloadImages,
                contentScale = ContentScale.Crop,
            )

            Row(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .offset(
                            y = -avatarSize * 0.1f,
                        ).padding(horizontal = Spacing.m),
                verticalAlignment = Alignment.Bottom,
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
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = Spacing.s),
        ) {
            Row {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    TextWithCustomEmojis(
                        text = user.displayName ?: user.username ?: "",
                        emojis = user.emojis,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        color = fullColor,
                        autoloadImages = autoloadImages,
                        onClick = {
                            onOpenUser?.invoke()
                        },
                    )
                    Text(
                        text =
                            buildAnnotatedString {
                                pushLink(
                                    LinkAnnotation.Clickable(
                                        tag = "user-handle",
                                        linkInteractionListener = { onOpenUser?.invoke() },
                                    ),
                                )
                                append(user.handle ?: user.username ?: "")
                                pop()
                            },
                        style = MaterialTheme.typography.titleMedium,
                        color = ancillaryColor,
                    )
                }

                if (relationshipStatus != null) {
                    UserRelationshipButton(
                        status = relationshipStatus,
                        locked = user.locked,
                        pending = user.relationshipStatusPending,
                        onClick = onRelationshipClicked,
                    )
                }
            }

            val followers = user.followers
            val following = user.following
            val annotatedContent =
                buildAnnotatedString {
                    append("$followers")
                    append(" ")
                    append(LocalStrings.current.accountFollower(followers))
                    append(" • ")
                    append("$following")
                    append(" ")
                    append(LocalStrings.current.accountFollowing(following))
                }
            Text(
                modifier = Modifier.padding(top = Spacing.s),
                text = annotatedContent,
                style = MaterialTheme.typography.labelMedium.copy(color = ancillaryColor),
            )

            user.bio?.takeIf { it.isNotEmpty() }?.let { bio ->
                ContentBody(
                    modifier = Modifier.padding(top = Spacing.s),
                    content = bio,
                    onOpenUrl = onOpenUrl,
                    onClick = onOpenUser,
                )
            }
        }
    }
}
