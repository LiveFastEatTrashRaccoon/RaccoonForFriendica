package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
fun ContentExtendedSocialInfo(
    favoriteCount: Int = 0,
    reblogCount: Int = 0,
    modifier: Modifier = Modifier,
    onOpenUsersFavorite: (() -> Unit)? = null,
    onOpenUsersReblog: (() -> Unit)? = null,
) {
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(CornerSize.xl))
                    .clickable {
                        if (reblogCount > 0) {
                            onOpenUsersReblog?.invoke()
                        }
                    }.padding(horizontal = Spacing.s),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(IconSize.s),
                imageVector = Icons.Default.Repeat,
                contentDescription = null,
                tint = ancillaryColor,
            )
            Text(
                text = LocalStrings.current.extendedSocialInfoReblogs(reblogCount),
                style = MaterialTheme.typography.labelMedium,
                color = ancillaryColor,
            )
        }
        Text(
            text = "•",
            style = MaterialTheme.typography.labelMedium,
            color = ancillaryColor,
        )
        Row(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(CornerSize.xl))
                    .clickable {
                        if (favoriteCount > 0) {
                            onOpenUsersFavorite?.invoke()
                        }
                    }.padding(horizontal = Spacing.s),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(IconSize.s),
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = ancillaryColor,
            )
            Text(
                text = LocalStrings.current.extendedSocialInfoFavorites(favoriteCount),
                style = MaterialTheme.typography.labelMedium,
                color = ancillaryColor,
            )
        }
    }
}
