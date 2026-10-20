package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original

@Composable
fun TimelineItem(
    post: PostModel,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
    pollEnabled: Boolean = true,
    extendedSocialInfoEnabled: Boolean = false,
    reshareAndReplyVisible: Boolean = true,
    followedHashtagsVisible: Boolean = true,
    blurNsfw: Boolean = true,
    autoloadImages: Boolean = true,
    maxTitleLines: Int = Int.MAX_VALUE,
    maxBodyLines: Int = Int.MAX_VALUE,
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
    val isReblog = post.reblog != null
    val isReply = post.inReplyTo != null
    val postToDisplay = post.original
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
    val inReplyToActionLabel =
        buildString {
            append(
                post.inReplyTo
                    ?.creator
                    ?.let { it.displayName ?: it.handle }
                    .orEmpty(),
            )
            if (isNotEmpty()) {
                append(": ")
            }
            append(LocalStrings.current.inReplyTo)
        }
    val rebloggedActionLabel =
        buildString {
            append(post.creator?.let { it.displayName ?: it.handle }.orEmpty())
            if (isNotEmpty()) {
                append(": ")
            }
            append(LocalStrings.current.rebloggedBy)
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
                val helperActions = mutableListOf<CustomAccessibilityAction>()
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
                if (reshareAndReplyVisible && isReblog && onOpenUser != null) {
                    helperActions +=
                        CustomAccessibilityAction(
                            label = rebloggedActionLabel,
                            action = {
                                post.creator?.let { onOpenUser(it) }
                                true
                            },
                        )
                }
                if (reshareAndReplyVisible && isReply && onOpenUser != null) {
                    helperActions +=
                        CustomAccessibilityAction(
                            label = inReplyToActionLabel,
                            action = {
                                post.inReplyTo?.creator?.let { onOpenUser(it) }
                                true
                            },
                        )
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
        when (layout) {
            TimelineLayout.Full ->
                FullTimelineItem(
                    actionsEnabled = actionsEnabled,
                    autoloadImages = autoloadImages,
                    blurNsfw = blurNsfw,
                    post = postToDisplay,
                    extendedSocialInfoEnabled = extendedSocialInfoEnabled,
                    originalCreator = post.creator?.takeIf { isReblog },
                    originalInReplyTo = post.inReplyTo?.takeIf { isReply },
                    maxBodyLines = maxBodyLines,
                    maxTitleLines = maxTitleLines,
                    options = options,
                    optionsMenuOpen = optionsMenuOpen,
                    pollEnabled = pollEnabled,
                    reshareAndReplyVisible = reshareAndReplyVisible,
                    followedHashtagsVisible = followedHashtagsVisible,
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
                    actionsEnabled = actionsEnabled,
                    autoloadImages = autoloadImages,
                    post = postToDisplay,
                    originalCreator = post.creator?.takeIf { isReblog },
                    originalInReplyTo = post.inReplyTo?.takeIf { isReply },
                    maxBodyLines = maxBodyLines,
                    maxTitleLines = maxTitleLines,
                    options = options,
                    optionsMenuOpen = optionsMenuOpen,
                    reshareAndReplyVisible = reshareAndReplyVisible,
                    followedHashtagsVisible = followedHashtagsVisible,
                    onBookmark = onBookmark,
                    onClick = onClick,
                    onFavorite = onFavorite,
                    onDislike = onDislike,
                    onOpenUrl = onOpenUrl,
                    onOpenUser = onOpenUser,
                    onSelectOption = onSelectOption,
                    onToggleOptionsMenu = {
                        optionsMenuOpen = it
                    },
                    onReblog = onReblog,
                    onReply = onReply,
                    onShowOriginal = onShowOriginal,
                    onOpenQuote = onOpenQuote,
                )

            TimelineLayout.Compact ->
                CompactTimelineItem(
                    actionsEnabled = actionsEnabled,
                    autoloadImages = autoloadImages,
                    blurNsfw = blurNsfw,
                    post = postToDisplay,
                    extendedSocialInfoEnabled = extendedSocialInfoEnabled,
                    originalCreator = post.creator?.takeIf { isReblog },
                    originalInReplyTo = post.inReplyTo?.takeIf { isReply },
                    maxBodyLines = maxBodyLines,
                    maxTitleLines = maxTitleLines,
                    options = options,
                    optionsMenuOpen = optionsMenuOpen,
                    pollEnabled = pollEnabled,
                    reshareAndReplyVisible = reshareAndReplyVisible,
                    followedHashtagsVisible = followedHashtagsVisible,
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

            TimelineLayout.Card ->
                CardTimelineItem(
                    actionsEnabled = actionsEnabled,
                    autoloadImages = autoloadImages,
                    blurNsfw = blurNsfw,
                    post = postToDisplay,
                    extendedSocialInfoEnabled = extendedSocialInfoEnabled,
                    originalCreator = post.creator?.takeIf { isReblog },
                    originalInReplyTo = post.inReplyTo?.takeIf { isReply },
                    maxBodyLines = maxBodyLines,
                    maxTitleLines = maxTitleLines,
                    options = options,
                    optionsMenuOpen = optionsMenuOpen,
                    pollEnabled = pollEnabled,
                    reshareAndReplyVisible = reshareAndReplyVisible,
                    followedHashtagsVisible = followedHashtagsVisible,
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
                    onOptionsMenuToggle = {
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
