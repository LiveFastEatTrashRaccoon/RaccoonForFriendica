package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
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
import androidx.compose.ui.unit.Dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
internal fun InReplyToInfo(
    modifier: Modifier = Modifier,
    user: UserModel?,
    iconSize: Dp = IconSize.s,
    onOpenUser: ((UserModel) -> Unit)? = null,
) {
    val creatorName = user?.let { it.displayName ?: it.handle }.orEmpty()
    val creatorAvatar = user?.avatar.orEmpty()
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = Icons.AutoMirrored.Default.Reply,
            contentDescription = null,
            tint = ancillaryColor,
        )
        Text(
            text = LocalStrings.current.timelineEntryInReplyTo,
            style = MaterialTheme.typography.bodyMedium,
            color = ancillaryColor,
        )
        if (creatorAvatar.isNotEmpty()) {
            CustomImage(
                modifier =
                    Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            if (user != null) {
                                onOpenUser?.invoke(user)
                            }
                        }.padding(Spacing.xxxs)
                        .size(iconSize)
                        .clip(RoundedCornerShape(iconSize / 2)),
                url = creatorAvatar,
                quality = FilterQuality.Low,
                contentScale = ContentScale.FillBounds,
            )
        } else {
            PlaceholderImage(
                modifier =
                    Modifier.clickable {
                        if (user != null) {
                            onOpenUser?.invoke(user)
                        }
                    },
                size = iconSize,
                title = creatorName,
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = creatorName,
            style = MaterialTheme.typography.bodyMedium,
            color = fullColor,
        )
    }
}
