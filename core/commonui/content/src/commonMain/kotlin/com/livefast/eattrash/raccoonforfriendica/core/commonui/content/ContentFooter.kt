package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.FeedbackButton

@Composable
fun ContentFooter(
    modifier: Modifier = Modifier,
    reblogged: Boolean = false,
    reblogCount: Int = 0,
    reblogLoading: Boolean = false,
    favorite: Boolean = false,
    favoriteCount: Int = 0,
    favoriteLoading: Boolean = false,
    bookmarked: Boolean = false,
    bookmarkLoading: Boolean = false,
    replyCount: Int = 0,
    onReply: (() -> Unit)? = null,
    onReblog: (() -> Unit)? = null,
    onFavorite: (() -> Unit)? = null,
    onBookmark: (() -> Unit)? = null,
) {
    val baseItemWidth = 70.dp
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        FooterItem(
            modifier = Modifier.width(baseItemWidth),
            icon = Icons.AutoMirrored.Default.Reply,
            value = replyCount,
            onClick = onReply,
        )
        FooterItem(
            modifier = Modifier.width(baseItemWidth),
            icon = Icons.Outlined.RocketLaunch,
            toggledIcon = Icons.Filled.RocketLaunch,
            value = reblogCount,
            toggled = reblogged,
            loading = reblogLoading,
            onClick = onReblog,
        )
        FooterItem(
            modifier = Modifier.width(baseItemWidth),
            icon = Icons.Default.FavoriteBorder,
            toggledIcon = Icons.Default.Favorite,
            value = favoriteCount,
            toggled = favorite,
            loading = favoriteLoading,
            onClick = onFavorite,
        )
        FooterItem(
            modifier = Modifier.width(baseItemWidth),
            icon = Icons.Default.BookmarkBorder,
            toggledIcon = Icons.Default.Bookmark,
            toggled = bookmarked,
            loading = bookmarkLoading,
            onClick = onBookmark,
        )
    }
}

@Composable
private fun FooterItem(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    value: Int = 0,
    toggledIcon: ImageVector? = null,
    toggled: Boolean = false,
    loading: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val toggledColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier =
            Modifier
                .clickable {
                    onClick?.invoke()
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (loading) {
                AnimatedVisibility(visible = loading) {
                    Box(
                        modifier = Modifier.size(IconSize.s),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color =
                            MaterialTheme.colorScheme.onBackground.copy(
                                ancillaryTextAlpha,
                            ),
                        )
                    }
                }
            } else {
                FeedbackButton(
                    imageVector = toggledIcon.takeIf { toggled } ?: icon,
                    tintColor = toggledColor.takeIf { toggled } ?: fullColor,
                    enabled = onClick != null,
                    onClick = {
                        onClick?.invoke()
                    },
                )
            }
            Text(
                modifier =
                Modifier
                    .padding(start = Spacing.xs)
                    .alpha(
                        if (loading || value == 0) 0f else 1f,
                    ),
                text = value.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = toggledColor.takeIf { toggled } ?: fullColor,
            )
        }
    }
}
