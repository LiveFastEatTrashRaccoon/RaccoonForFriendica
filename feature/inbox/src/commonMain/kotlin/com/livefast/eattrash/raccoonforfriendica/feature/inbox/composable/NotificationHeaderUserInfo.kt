package com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel

@Composable
internal fun NotificationHeaderUserInfo(
    modifier: Modifier = Modifier,
    account: AccountModel,
    onOpenUser: ((AccountModel) -> Unit)? = null,
) {
    val iconSize = IconSize.s
    val creatorName = account?.let { it.displayName ?: it.handle }.orEmpty()
    val creatorAvatar = account?.avatar.orEmpty()
    val fullColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (creatorAvatar.isNotEmpty()) {
            CustomImage(
                modifier =
                    Modifier
                        .clickable {
                            if (account != null) {
                                onOpenUser?.invoke(account)
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
                        if (account != null) {
                            onOpenUser?.invoke(account)
                        }
                    },
                size = iconSize,
                title = creatorName,
            )
        }

        Text(
            text = creatorName,
            style = MaterialTheme.typography.bodyLarge,
            color = fullColor,
        )
    }
}
