package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.LocalUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original

@Composable
fun TimelineReplyItem(
    post: PostModel,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
    pollEnabled: Boolean = true,
    extendedSocialInfoEnabled: Boolean = false,
    blurNsfw: Boolean = true,
    autoloadImages: Boolean = true,
    layout: TimelineLayout = TimelineLayout.Full,
    options: List<Option> = emptyList(),
    onOpenUrl: ((String, Boolean) -> Unit)? = null,
    onClick: ((PostModel) -> Unit)? = null,
    onOpenUser: ((UserModel) -> Unit)? = null,
    onReply: ((PostModel) -> Unit)? = null,
    onReblog: ((PostModel) -> Unit)? = null,
    onFavorite: ((PostModel) -> Unit)? = null,
    onDislike: ((PostModel) -> Unit)? = null,
    onBookmark: ((PostModel) -> Unit)? = null,
    onOpenUsersFavorite: ((PostModel) -> Unit)? = null,
    onOpenUsersReblog: ((PostModel) -> Unit)? = null,
    onOpenImage: ((List<String>, Int, List<Int>) -> Unit)? = null,
    onSelectOption: ((OptionId) -> Unit)? = null,
    onPollVote: ((PostModel, List<Int>) -> Unit)? = null,
    onShowOriginal: (() -> Unit)? = null,
    onOpenQuote: ((PostModel) -> Unit)? = null,
) {
    val postToDisplay = post.original
    val depthZeroBased = (post.depth - 1).coerceAtLeast(0)
    val themeRepository = LocalUiDeps.current.themeRepository
    val barWidth = 3.dp
    val barColor = themeRepository.getCommentBarColor(depthZeroBased)
    var barHeight by remember { mutableStateOf(0.dp) }
    val indentAmount = Spacing.s + (barWidth + Spacing.s) * depthZeroBased
    val localDensity = LocalDensity.current
    var optionsMenuOpen by remember { mutableStateOf(false) }
    val replyActionLabel =
        buildString {
            append(LocalStrings.current.actionReply)
            if (postToDisplay.replyCount > 0) {
                append(": ")
                append(postToDisplay.replyCount)
            }
        }
    val reblogActionLabel =
        buildString {
            append(LocalStrings.current.actionReblog)
            if (postToDisplay.reblogCount > 0) {
                append(": ")
                append(postToDisplay.reblogCount)
            }
        }
    val favoriteActionLabel =
        buildString {
            if (postToDisplay.favorite) {
                append(LocalStrings.current.actionRemoveFromFavorites)
            } else {
                append(LocalStrings.current.actionAddToFavorites)
            }
            if (postToDisplay.favoriteCount > 0) {
                append(": ")
                append(postToDisplay.favoriteCount)
            }
        }
    val dislikeActionLabel =
        buildString {
            if (postToDisplay.disliked) {
                append(LocalStrings.current.actionRemoveDislike)
            } else {
                append(LocalStrings.current.actionDislike)
            }
            if (postToDisplay.dislikesCount > 0) {
                append(": ")
                append(postToDisplay.dislikesCount)
            }
        }
    val bookmarkActionLabel =
        buildString {
            if (postToDisplay.bookmarked) {
                append(LocalStrings.current.actionRemoveFromBookmarks)
            } else {
                append(LocalStrings.current.actionAddToBookmarks)
            }
        }
    val userActionLabel =
        buildString {
            append(postToDisplay.creator?.let { it.displayName ?: it.handle }.orEmpty())
            if (isNotEmpty()) {
                append(": ")
            }
            append(LocalStrings.current.postTitle)
            append(" ")
            append(LocalStrings.current.postBy)
        }
    val optionsActionLabel = LocalStrings.current.actionOpenOptions

    Box(
        modifier =
        modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onClick(postToDisplay)
                    }
                } else {
                    Modifier
                },
            ).semantics(mergeDescendants = true) {
                val helperActions: MutableList<CustomAccessibilityAction> = mutableListOf()
                if (actionsEnabled) {
                    if (onReply != null) {
                        helperActions +=
                            CustomAccessibilityAction(
                                label = replyActionLabel,
                                action = {
                                    onReply(postToDisplay)
                                    true
                                },
                            )
                    }
                    if (onReblog != null) {
                        helperActions +=
                            CustomAccessibilityAction(
                                label = reblogActionLabel,
                                action = {
                                    onReblog(postToDisplay)
                                    true
                                },
                            )
                    }
                    if (onFavorite != null) {
                        helperActions +=
                            CustomAccessibilityAction(
                                label = favoriteActionLabel,
                                action = {
                                    onFavorite(postToDisplay)
                                    true
                                },
                            )
                    }
                    if (onDislike != null) {
                        helperActions +=
                            CustomAccessibilityAction(
                                label = dislikeActionLabel,
                                action = {
                                    onDislike(postToDisplay)
                                    true
                                },
                            )
                    }
                    if (onBookmark != null) {
                        helperActions +=
                            CustomAccessibilityAction(
                                label = bookmarkActionLabel,
                                action = {
                                    onBookmark(postToDisplay)
                                    true
                                },
                            )
                    }
                }
                if (onOpenUser != null) {
                    helperActions +=
                        CustomAccessibilityAction(
                            label = userActionLabel,
                            action = {
                                postToDisplay.creator?.let { onOpenUser(it) }
                                true
                            },
                        )
                }
                if (options.isNotEmpty()) {
                    helperActions +=
                        CustomAccessibilityAction(
                            label = optionsActionLabel,
                            action = {
                                optionsMenuOpen = true
                                true
                            },
                        )
                }

                if (helperActions.isNotEmpty()) {
                    customActions = helperActions
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            // comment bar
            Box(
                modifier =
                Modifier
                    .padding(start = indentAmount)
                    .size(width = barWidth, height = barHeight)
                    .background(
                        color = barColor,
                        shape = RoundedCornerShape(barWidth / 2),
                    ),
            )

            // comment content
            val contentModifier =
                Modifier.onGloballyPositioned {
                    barHeight =
                        with(localDensity) {
                            it.size
                                .toSize()
                                .height
                                .toDp()
                        }
                }
            when (layout) {
                TimelineLayout.Full, TimelineLayout.Card ->
                    FullTimelineItem(
                        modifier = contentModifier,
                        actionsEnabled = actionsEnabled,
                        autoloadImages = autoloadImages,
                        blurNsfw = blurNsfw,
                        post = postToDisplay,
                        extendedSocialInfoEnabled = extendedSocialInfoEnabled,
                        options = options,
                        optionsMenuOpen = optionsMenuOpen,
                        pollEnabled = pollEnabled,
                        onBookmark = onBookmark,
                        onClick = onClick,
                        onFavorite = onFavorite,
                        onDislike = onDislike,
                        onOpenImage = onOpenImage,
                        onOpenUrl = onOpenUrl,
                        onOpenUser = onOpenUser,
                        onOpenUsersFavorite = onOpenUsersFavorite,
                        onOpenUsersReblog = onOpenUsersReblog,
                        onSelectOption = onSelectOption,
                        onToggleOptionsMenu = {
                            optionsMenuOpen = it
                        },
                        onPollVote = onPollVote,
                        onReblog = onReblog,
                        onReply = onReply,
                        onShowOriginal = onShowOriginal,
                        onOpenQuote = onOpenQuote,
                    )

                TimelineLayout.DistractionFree ->
                    DistractionFreeTimelineItem(
                        modifier = contentModifier,
                        actionsEnabled = actionsEnabled,
                        autoloadImages = autoloadImages,
                        post = postToDisplay,
                        extendedSocialInfoEnabled = extendedSocialInfoEnabled,
                        options = options,
                        optionsMenuOpen = optionsMenuOpen,
                        onBookmark = onBookmark,
                        onClick = onClick,
                        onFavorite = onFavorite,
                        onDislike = onDislike,
                        onOpenUrl = onOpenUrl,
                        onOpenUser = onOpenUser,
                        onOpenUsersFavorite = onOpenUsersFavorite,
                        onOpenUsersReblog = onOpenUsersReblog,
                        onSelectOption = onSelectOption,
                        onToggleOptionsMenu = {
                            optionsMenuOpen = it
                        },
                        onPollVote = onPollVote,
                        onReblog = onReblog,
                        onReply = onReply,
                        onShowOriginal = onShowOriginal,
                        onOpenQuote = onOpenQuote,
                    )

                TimelineLayout.Compact ->
                    CompactTimelineItem(
                        modifier = contentModifier,
                        actionsEnabled = actionsEnabled,
                        autoloadImages = autoloadImages,
                        blurNsfw = blurNsfw,
                        post = postToDisplay,
                        extendedSocialInfoEnabled = extendedSocialInfoEnabled,
                        options = options,
                        optionsMenuOpen = optionsMenuOpen,
                        onBookmark = onBookmark,
                        onClick = onClick,
                        onFavorite = onFavorite,
                        onDislike = onDislike,
                        onOpenImage = onOpenImage,
                        onOpenUrl = onOpenUrl,
                        onOpenUser = onOpenUser,
                        onOpenUsersFavorite = onOpenUsersFavorite,
                        onOpenUsersReblog = onOpenUsersReblog,
                        onSelectOption = onSelectOption,
                        onToggleOptionsMenu = {
                            optionsMenuOpen = it
                        },
                        onPollVote = onPollVote,
                        onReblog = onReblog,
                        onReply = onReply,
                        onShowOriginal = onShowOriginal,
                        onOpenQuote = onOpenQuote,
                    )
            }
        }
    }
}
