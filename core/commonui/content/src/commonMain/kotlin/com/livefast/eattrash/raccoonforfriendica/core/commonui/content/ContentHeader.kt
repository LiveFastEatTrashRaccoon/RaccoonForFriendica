package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
fun ContentHeader(
    modifier: Modifier = Modifier,
    user: UserModel? = null,
    date: String? = null,
    isEdited: Boolean = false,
    iconSize: Dp = IconSize.l,
    onOpenUser: ((UserModel) -> Unit)? = null,
) {
    val creatorName = user?.let { it.displayName ?: it.handle }.orEmpty()
    val creatorAvatar = user?.avatar.orEmpty()
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Row(
        modifier =
            modifier.clickable {
                if (user != null) {
                    onOpenUser?.invoke(user)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        if (creatorAvatar.isNotEmpty()) {
            CustomImage(
                modifier =
                    Modifier
                        .padding(Spacing.xxxs)
                        .size(iconSize)
                        .clip(RoundedCornerShape(iconSize / 2)),
                url = creatorAvatar,
                quality = FilterQuality.Low,
                contentScale = ContentScale.FillBounds,
            )
        } else {
            PlaceholderImage(
                size = iconSize,
                title = creatorName,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = creatorName,
                style = MaterialTheme.typography.bodySmall,
                color = fullColor,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text =
                        buildString {
                            if (!user?.handle.isNullOrBlank()) {
                                append(user?.handle)
                            }
                            if (!date.isNullOrBlank()) {
                                if (isNotEmpty()) {
                                    append(" •")
                                }
                                append(date.prettifyDate())
                                if (isEdited) {
                                    append(" (")
                                    append(LocalStrings.current.infoEdited)
                                    append(")")
                                }
                            }
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = ancillaryColor,
                )
            }
        }
    }
}
