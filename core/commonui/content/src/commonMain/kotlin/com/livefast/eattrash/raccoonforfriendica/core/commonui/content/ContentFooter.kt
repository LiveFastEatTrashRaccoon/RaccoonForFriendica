package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.FeedbackButton

@Composable
fun ContentFooter(
    modifier: Modifier = Modifier,
    reblogged: Boolean = false,
    reblogCount: Int = 0,
    favorite: Boolean = false,
    favoriteCount: Int = 0,
    bookmarked: Boolean = false,
    replyCount: Int = 0,
    onReply: (() -> Unit)? = null,
    onReblog: (() -> Unit)? = null,
    onFavorite: (() -> Unit)? = null,
    onBookmark: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        FooterItem(
            icon = Icons.AutoMirrored.Default.Reply,
            value = replyCount.takeIf { it > 0 },
            onClick = onReply,
        )
        FooterItem(
            icon = Icons.Default.Repeat,
            value = reblogCount.takeIf { it > 0 },
            toggled = reblogged,
            onClick = onReblog,
        )
        FooterItem(
            icon = Icons.Default.FavoriteBorder,
            toggledIcon = Icons.Default.Favorite,
            value = favoriteCount.takeIf { it > 0 },
            toggled = favorite,
            onClick = onFavorite,
        )
        FooterItem(
            icon = Icons.Default.BookmarkBorder,
            toggledIcon = Icons.Default.Bookmark,
            toggled = bookmarked,
            onClick = onBookmark,
        )
    }
}

@Composable
private fun FooterItem(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    value: Int? = null,
    toggledIcon: ImageVector? = null,
    toggled: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val toggledColor = MaterialTheme.colorScheme.primary
    Row(
        modifier =
            modifier
                .clickable {
                    onClick?.invoke()
                }.padding(horizontal = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeedbackButton(
            imageVector = toggledIcon.takeIf { toggled } ?: icon,
            tintColor = toggledColor.takeIf { toggled } ?: fullColor,
            enabled = onClick != null,
            onClick = {
                onClick?.invoke()
            },
        )

        if (value != null) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = toggledColor.takeIf { toggled } ?: fullColor,
            )
        }
    }
}
