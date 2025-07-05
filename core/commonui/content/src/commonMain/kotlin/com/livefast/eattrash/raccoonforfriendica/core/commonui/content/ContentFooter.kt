package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
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
    disliked: Boolean = false,
    dislikeCount: Int = 0,
    dislikeLoading: Boolean = false,
    options: List<Option> = emptyList(),
    optionsMenuOpen: Boolean = false,
    onSelectOption: ((OptionId) -> Unit)? = null,
    onToggleOptionsMenu: ((Boolean) -> Unit)? = null,
    onReply: (() -> Unit)? = null,
    onReblog: (() -> Unit)? = null,
    onFavorite: (() -> Unit)? = null,
    onDislike: (() -> Unit)? = null,
    onBookmark: (() -> Unit)? = null,
) {
    val canLikeAndDislike = onFavorite != null && onDislike != null
    val itemModifier = Modifier.clearAndSetSemantics { }.padding(horizontal = Spacing.s)
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }

    Row(
        modifier =
        modifier.padding(
            vertical = Spacing.xs,
            horizontal = Spacing.xxs,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (onReply != null) {
            FooterItem(
                modifier = itemModifier,
                icon = Icons.AutoMirrored.Default.Reply,
                contentDescription = null,
                value = replyCount,
                onClick = onReply,
            )
        }
        if (onReblog != null) {
            FooterItem(
                modifier = itemModifier,
                icon = Icons.Outlined.RocketLaunch,
                toggledIcon = Icons.Filled.RocketLaunch,
                contentDescription = null,
                value = reblogCount,
                toggled = reblogged,
                loading = reblogLoading,
                onClick = onReblog,
            )
        }
        if (onFavorite != null) {
            FooterItem(
                modifier = itemModifier,
                icon =
                if (canLikeAndDislike) {
                    Icons.Outlined.ThumbUp
                } else {
                    Icons.Default.FavoriteBorder
                },
                toggledIcon =
                if (canLikeAndDislike) {
                    Icons.Default.ThumbUp
                } else {
                    Icons.Default.Favorite
                },
                contentDescription = null,
                value = favoriteCount,
                toggled = favorite,
                loading = favoriteLoading,
                onClick = onFavorite,
            )
        }
        if (onDislike != null) {
            FooterItem(
                modifier = itemModifier,
                icon = Icons.Outlined.ThumbDown,
                toggledIcon = Icons.Default.ThumbDown,
                contentDescription = null,
                value = dislikeCount,
                toggled = disliked,
                loading = dislikeLoading,
                onClick = onDislike,
            )
        }
        if (onBookmark != null) {
            FooterItem(
                modifier = itemModifier,
                icon = Icons.Default.BookmarkBorder,
                toggledIcon = Icons.Default.Bookmark,
                contentDescription = null,
                toggled = bookmarked,
                loading = bookmarkLoading,
                onClick = onBookmark,
            )
        }

        if (options.isNotEmpty()) {
            val noPreviousActions = listOf(
                onReblog,
                onReply,
                onBookmark,
                onFavorite,
                onDislike,
            ).all { it == null }
            if (noPreviousActions) {
                Spacer(Modifier.weight(1f))
            }
            Box(
                modifier = itemModifier,
            ) {
                IconButton(
                    modifier =
                    Modifier
                        .padding(bottom = Spacing.xs, end = Spacing.s)
                        .size(height = IconSize.m, width = IconSize.l)
                        .onGloballyPositioned {
                            optionsOffset = it.positionInParent()
                        }.clearAndSetSemantics { },
                    onClick = {
                        onToggleOptionsMenu?.invoke(true)
                    },
                ) {
                    Icon(
                        modifier = Modifier.size(IconSize.s),
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }

                CustomDropDown(
                    expanded = optionsMenuOpen,
                    onDismiss = {
                        onToggleOptionsMenu?.invoke(false)
                    },
                    offset =
                    with(LocalDensity.current) {
                        DpOffset(
                            x = optionsOffset.x.toDp(),
                            y = optionsOffset.y.toDp(),
                        )
                    },
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(option.label)
                            },
                            onClick = {
                                onToggleOptionsMenu?.invoke(false)
                                onSelectOption?.invoke(option.id)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FooterItem(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
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
                    contentDescription = contentDescription,
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
