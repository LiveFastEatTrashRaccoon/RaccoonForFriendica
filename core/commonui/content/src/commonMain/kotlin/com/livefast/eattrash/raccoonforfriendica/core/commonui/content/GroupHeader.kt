package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.PreviewWrapper
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
fun GroupHeader(
    user: UserModel?,
    modifier: Modifier = Modifier,
    autoloadImages: Boolean = true,
    onOpenImage: ((String) -> Unit)? = null,
    onOpenFollowers: (() -> Unit)? = null,
    onOpenUrl: ((String, Boolean) -> Unit)? = null,
    onRelationshipClick: ((RelationshipStatusNextAction) -> Unit)? = null,
) {
    val avatar = user?.avatar.orEmpty()
    val avatarSize = 50.dp
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val relationshipStatus = user?.relationshipStatus
    val notificationStatus = user?.notificationStatus

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.m),
        verticalArrangement = Arrangement.spacedBy(Spacing.m),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            if (avatar.isNotEmpty() && autoloadImages) {
                CustomImage(
                    modifier =
                    Modifier
                        .size(avatarSize)
                        .clip(RoundedCornerShape(avatarSize / 2))
                        .then(
                            if (onOpenImage != null) {
                                Modifier.clickable { onOpenImage(avatar) }
                            } else {
                                Modifier
                            },
                        ),
                    url = avatar,
                    quality = FilterQuality.Low,
                    contentScale = ContentScale.Crop,
                )
            } else {
                PlaceholderImage(
                    size = avatarSize,
                    title = user?.displayName ?: user?.handle ?: "?",
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                TextWithCustomEmojis(
                    text = user?.displayName ?: user?.username ?: "",
                    emojis = user?.emojis.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    autoloadImages = autoloadImages,
                    color = fullColor,
                )

                Text(
                    text = user?.handle ?: user?.username ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ancillaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                val followers = user?.followers ?: 0
                Text(
                    modifier =
                    Modifier
                        .clip(RoundedCornerShape(CornerSize.s))
                        .then(
                            if (followers > 0 && onOpenFollowers != null) {
                                Modifier.clickable { onOpenFollowers() }
                            } else {
                                Modifier
                            },
                        ),
                    text = LocalStrings.current.accountFollower(followers),
                    style = MaterialTheme.typography.labelMedium,
                    color = ancillaryColor,
                )
            }

            if (relationshipStatus != null) {
                UserRelationshipButton(
                    status = relationshipStatus,
                    locked = user.locked,
                    pending = user.relationshipStatusPending,
                    onClick = onRelationshipClick,
                )
            }
        }

        user?.bio?.takeIf { it.isNotEmpty() }?.let { bio ->
            ContentBody(
                content = bio,
                emojis = user.emojis,
                onOpenUrl = onOpenUrl?.let { block -> { url -> block(url, true) } },
            )
        }
    }
}

@Composable
@Preview
private fun GroupHeaderPreview() {
    PreviewWrapper {
        GroupHeader(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            user = UserModel(
                id = "1",
                displayName = "Android Devs",
                handle = "!android@poliverso.org",
                bio = "A community for Android developers to discuss the platform.",
                followers = 1337,
                group = true,
                following = 42,
                relationshipStatus = RelationshipStatus.Following,
                notificationStatus = NotificationStatus.Enabled,
            ),
        )
    }
}
