package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
internal fun ReblogInfo(
    user: UserModel?,
    modifier: Modifier = Modifier,
    autoloadImages: Boolean = true,
    iconSize: Dp = IconSize.s,
    onOpenUser: ((UserModel) -> Unit)? = null,
) {
    val creatorName = user?.let { it.displayName ?: it.handle }.orEmpty()
    val creatorAvatar = user?.avatar.orEmpty()
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val onOpenUserModifier =
        if (onOpenUser != null) {
            Modifier
                .clearAndSetSemantics { }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    if (user != null) {
                        onOpenUser.invoke(user)
                    }
                }
        } else {
            Modifier
        }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        // There is no need for a contentDescription here as this is decorative image with no click handler
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = Icons.Default.Repeat,
            contentDescription = null,
            tint = ancillaryColor,
        )
        Text(
            text = LocalStrings.current.timelineEntryRebloggedBy,
            style = MaterialTheme.typography.bodyMedium,
            color = ancillaryColor,
        )
        if (creatorAvatar.isNotEmpty() && autoloadImages) {
            CustomImage(
                modifier =
                Modifier
                    .size(iconSize)
                    .then(onOpenUserModifier)
                    .padding(Spacing.xxxs)
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
            emojis = user?.emojis.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = fullColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            autoloadImages = autoloadImages,
            onClick = {
                user?.also { onOpenUser?.invoke(it) }
            },
        )
    }
}
