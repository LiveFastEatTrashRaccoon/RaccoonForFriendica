package com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TextWithCustomEmojis
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
internal fun NotificationHeaderUserInfo(
    modifier: Modifier = Modifier,
    user: UserModel,
    autoloadImages: Boolean = true,
    onOpenUser: (() -> Unit)? = null,
) {
    val iconSize = IconSize.s
    val creatorName = user.let { it.displayName ?: it.handle }.orEmpty()
    val creatorAvatar = user.avatar.orEmpty()
    val fullColor = MaterialTheme.colorScheme.onBackground
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

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (creatorAvatar.isNotEmpty() && autoloadImages) {
            CustomImage(
                modifier =
                    Modifier
                        .size(iconSize)
                        .then(onOpenUserModifier)
                        .clip(RoundedCornerShape(iconSize / 2)),
                url = creatorAvatar,
                quality = FilterQuality.Low,
                contentScale = ContentScale.FillBounds,
            )
        } else {
            PlaceholderImage(
                modifier = onOpenUserModifier,
                size = iconSize,
                title = creatorName,
            )
        }

        TextWithCustomEmojis(
            text = creatorName,
            emojis = user.emojis,
            style = MaterialTheme.typography.bodyMedium,
            color = fullColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            autoloadImages = autoloadImages,
            onClick = {
                onOpenUser?.invoke()
            },
        )
    }
}
