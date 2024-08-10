package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon

private object AnnotationConstants {
    const val TAG = "action"
    const val ACTION_OPEN_FOLLOWER = "followers"
    const val ACTION_OPEN_FOLLOWING = "following"
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun UserHeader(
    user: UserModel?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onOpenFollowers: (() -> Unit)? = null,
    onOpenFollowing: (() -> Unit)? = null,
    onOpenUrl: ((String) -> Unit)? = null,
    onRelationshipClicked: ((RelationshipStatusNextAction) -> Unit)? = null,
    onNotificationsClicked: (() -> Unit)? = null,
) {
    val banner = user?.header.orEmpty()
    val avatar = user?.avatar.orEmpty()
    val avatarSize = 110.dp
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val relationshipStatus = user?.relationshipStatus
    val notificationStatus = user?.notificationStatus

    Column(
        modifier =
            modifier.clickable {
                onClick?.invoke()
            },
    ) {
        Box {
            CustomImage(
                modifier =
                    Modifier
                        .padding(bottom = avatarSize * 0.8f)
                        .aspectRatio(16 / 9f),
                url = banner,
                contentScale = ContentScale.FillBounds,
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
                if (avatar.isNotEmpty()) {
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
                        title = user?.displayName ?: user?.handle ?: "?",
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    val followerDesc = LocalStrings.current.accountFollower
                    val followingDesc = LocalStrings.current.accountFollowing
                    val annotatedContent =
                        buildAnnotatedString {
                            withAnnotation(
                                tag = AnnotationConstants.TAG,
                                annotation = AnnotationConstants.ACTION_OPEN_FOLLOWER,
                            ) {
                                withStyle(SpanStyle(color = fullColor)) {
                                    append("${user?.followers ?: 0}")
                                }
                                append(" ")
                                append(followerDesc)
                            }
                            append(" • ")
                            withAnnotation(
                                tag = AnnotationConstants.TAG,
                                annotation = AnnotationConstants.ACTION_OPEN_FOLLOWING,
                            ) {
                                withStyle(SpanStyle(color = fullColor)) {
                                    append("${user?.following ?: 0}")
                                }
                                append(" ")
                                append(followingDesc)
                            }
                        }
                    ClickableText(
                        text = annotatedContent,
                        style = MaterialTheme.typography.labelMedium.copy(color = ancillaryColor),
                        onClick = { offset ->
                            val annotation =
                                annotatedContent
                                    .getStringAnnotations(start = offset, end = offset)
                                    .firstOrNull()

                            when (annotation?.item) {
                                AnnotationConstants.ACTION_OPEN_FOLLOWER -> onOpenFollowers?.invoke()
                                AnnotationConstants.ACTION_OPEN_FOLLOWING -> onOpenFollowing?.invoke()
                                else -> Unit
                            }
                        },
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                    ) {
                        if (notificationStatus != null) {
                            Icon(
                                modifier =
                                    Modifier
                                        .size(IconSize.l)
                                        .clickable {
                                            onNotificationsClicked?.invoke()
                                        }.border(
                                            width = Dp.Hairline,
                                            color =
                                                MaterialTheme.colorScheme.onBackground.copy(
                                                    ancillaryTextAlpha,
                                                ),
                                            shape = CircleShape,
                                        ).padding(5.dp),
                                imageVector = notificationStatus.toIcon(),
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null,
                            )
                        }
                        if (relationshipStatus != null) {
                            UserRelationshipButton(
                                relationshipStatus = relationshipStatus,
                                pending = user.relationshipStatusPending,
                                onClick = onRelationshipClicked,
                            )
                        }
                    }

                    if (user?.group == true) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = Modifier.size(IconSize.s),
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = ancillaryColor,
                            )
                            Text(
                                text = LocalStrings.current.accountGroup,
                                style = MaterialTheme.typography.labelMedium,
                                color = ancillaryColor,
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = user?.displayName ?: user?.username ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = fullColor,
            )
            Text(
                text = user?.handle ?: user?.username ?: "",
                style = MaterialTheme.typography.titleSmall,
                color = ancillaryColor,
            )
            user?.bio?.takeIf { it.isNotEmpty() }?.let { bio ->
                ContentBody(
                    content = bio,
                    onOpenUrl = onOpenUrl,
                    onClick = onClick,
                )
            }
        }
    }
}
