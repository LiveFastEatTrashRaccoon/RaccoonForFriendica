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
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original

@Composable
fun TimelineItem(
    entry: TimelineEntryModel,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
    pollEnabled: Boolean = true,
    extendedSocialInfoEnabled: Boolean = false,
    reshareAndReplyVisible: Boolean = true,
    blurNsfw: Boolean = true,
    autoloadImages: Boolean = true,
    maxTitleLines: Int = Int.MAX_VALUE,
    maxBodyLines: Int = Int.MAX_VALUE,
    layout: TimelineLayout = TimelineLayout.Full,
    options: List<Option> = emptyList(),
    onOpenUrl: ((String) -> Unit)? = null,
    onClick: ((TimelineEntryModel) -> Unit)? = null,
    onOpenUser: ((UserModel) -> Unit)? = null,
    onReply: ((TimelineEntryModel) -> Unit)? = null,
    onReblog: ((TimelineEntryModel) -> Unit)? = null,
    onFavorite: ((TimelineEntryModel) -> Unit)? = null,
    onDislike: ((TimelineEntryModel) -> Unit)? = null,
    onBookmark: ((TimelineEntryModel) -> Unit)? = null,
    onOpenUsersFavorite: ((TimelineEntryModel) -> Unit)? = null,
    onOpenUsersReblog: ((TimelineEntryModel) -> Unit)? = null,
    onOpenImage: ((List<String>, Int, List<Int>) -> Unit)? = null,
    onOptionSelected: ((OptionId) -> Unit)? = null,
    onPollVote: ((TimelineEntryModel, List<Int>) -> Unit)? = null,
) {
    val isReblog = entry.reblog != null
    val isReply = entry.inReplyTo != null
    val entryToDisplay = entry.original
    var optionsMenuOpen by remember { mutableStateOf(false) }

    val replyActionLabel =
        buildString {
            append(LocalStrings.current.actionReply)
            if (entryToDisplay.replyCount > 0) {
                append(": ")
                append(entryToDisplay.replyCount)
            }
        }
    val reblogActionLabel =
        buildString {
            append(LocalStrings.current.actionReblog)
            if (entryToDisplay.reblogCount > 0) {
                append(": ")
                append(entryToDisplay.reblogCount)
            }
        }
    val favoriteActionLabel =
        buildString {
            if (entryToDisplay.favorite) {
                append(LocalStrings.current.actionRemoveFromFavorites)
            } else {
                append(LocalStrings.current.actionAddToFavorites)
            }
            if (entryToDisplay.favoriteCount > 0) {
                append(": ")
                append(entryToDisplay.favoriteCount)
            }
        }
    val dislikeActionLabel =
        buildString {
            if (entryToDisplay.disliked) {
                append(LocalStrings.current.actionRemoveDislike)
            } else {
                append(LocalStrings.current.actionDislike)
            }
            if (entryToDisplay.dislikesCount > 0) {
                append(": ")
                append(entryToDisplay.dislikesCount)
            }
        }
    val bookmarkActionLabel =
        buildString {
            if (entryToDisplay.bookmarked) {
                append(LocalStrings.current.actionRemoveFromBookmarks)
            } else {
                append(LocalStrings.current.actionAddToBookmarks)
            }
        }
    val inReplyToActionLabel =
        buildString {
            append(
                entry.inReplyTo
                    ?.creator
                    ?.let { it.displayName ?: it.handle }
                    .orEmpty(),
            )
            if (isNotEmpty()) {
                append(": ")
            }
            append(LocalStrings.current.timelineEntryInReplyTo)
        }
    val rebloggedActionLabel =
        buildString {
            append(entry.creator?.let { it.displayName ?: it.handle }.orEmpty())
            if (isNotEmpty()) {
                append(": ")
            }
            append(LocalStrings.current.timelineEntryRebloggedBy)
        }
    val userActionLabel =
        buildString {
            append(entryToDisplay.creator?.let { it.displayName ?: it.handle }.orEmpty())
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
                            onClick(entryToDisplay)
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
                                        onReply(entryToDisplay)
                                        true
                                    },
                                )
                        }
                        if (onReblog != null) {
                            helperActions +=
                                CustomAccessibilityAction(
                                    label = reblogActionLabel,
                                    action = {
                                        onReblog(entryToDisplay)
                                        true
                                    },
                                )
                        }
                        if (onFavorite != null) {
                            helperActions +=
                                CustomAccessibilityAction(
                                    label = favoriteActionLabel,
                                    action = {
                                        onFavorite(entryToDisplay)
                                        true
                                    },
                                )
                        }
                        if (onDislike != null) {
                            helperActions +=
                                CustomAccessibilityAction(
                                    label = dislikeActionLabel,
                                    action = {
                                        onDislike(entryToDisplay)
                                        true
                                    },
                                )
                        }
                        if (onBookmark != null) {
                            helperActions +=
                                CustomAccessibilityAction(
                                    label = bookmarkActionLabel,
                                    action = {
                                        onBookmark(entryToDisplay)
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
                                    entry.creator?.let { onOpenUser(it) }
                                    true
                                },
                            )
                    }
                    if (reshareAndReplyVisible && isReply && onOpenUser != null) {
                        helperActions +=
                            CustomAccessibilityAction(
                                label = inReplyToActionLabel,
                                action = {
                                    entry.inReplyTo?.creator?.let { onOpenUser(it) }
                                    true
                                },
                            )
                    }
                    if (onOpenUser != null) {
                        helperActions +=
                            CustomAccessibilityAction(
                                label = userActionLabel,
                                action = {
                                    entryToDisplay.creator?.let { onOpenUser(it) }
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
                        if (helperActions.isNotEmpty()) {
                            customActions = helperActions
                        }
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
                    entryToDisplay = entryToDisplay,
                    extendedSocialInfoEnabled = extendedSocialInfoEnabled,
                    originalCreator = entry.creator?.takeIf { isReblog },
                    originalInReplyTo = entry.inReplyTo?.takeIf { isReply },
                    maxBodyLines = maxBodyLines,
                    maxTitleLines = maxTitleLines,
                    options = options,
                    optionsMenuOpen = optionsMenuOpen,
                    pollEnabled = pollEnabled,
                    reshareAndReplyVisible = reshareAndReplyVisible,
                    onBookmark = onBookmark,
                    onClick = onClick,
                    onFavorite = onFavorite,
                    onDislike = onDislike,
                    onOpenImage = onOpenImage,
                    onOpenUrl = onOpenUrl,
                    onOpenUser = onOpenUser,
                    onOpenUsersFavorite = onOpenUsersFavorite,
                    onOpenUsersReblog = onOpenUsersReblog,
                    onOptionSelected = onOptionSelected,
                    onOptionsMenuToggled = {
                        optionsMenuOpen = it
                    },
                    onPollVote = onPollVote,
                    onReblog = onReblog,
                    onReply = onReply,
                )

            TimelineLayout.DistractionFree ->
                DistractionFreeTimelineItem(
                    actionsEnabled = actionsEnabled,
                    autoloadImages = autoloadImages,
                    entryToDisplay = entryToDisplay,
                    originalCreator = entry.creator?.takeIf { isReblog },
                    originalInReplyTo = entry.inReplyTo?.takeIf { isReply },
                    maxBodyLines = maxBodyLines,
                    maxTitleLines = maxTitleLines,
                    options = options,
                    optionsMenuOpen = optionsMenuOpen,
                    reshareAndReplyVisible = reshareAndReplyVisible,
                    onBookmark = onBookmark,
                    onClick = onClick,
                    onFavorite = onFavorite,
                    onDislike = onDislike,
                    onOpenUrl = onOpenUrl,
                    onOpenUser = onOpenUser,
                    onOptionSelected = onOptionSelected,
                    onOptionsMenuToggled = {
                        optionsMenuOpen = it
                    },
                    onReblog = onReblog,
                    onReply = onReply,
                )

            TimelineLayout.Compact ->
                CompactTimelineItem(
                    actionsEnabled = actionsEnabled,
                    autoloadImages = autoloadImages,
                    blurNsfw = blurNsfw,
                    entryToDisplay = entryToDisplay,
                    extendedSocialInfoEnabled = extendedSocialInfoEnabled,
                    originalCreator = entry.creator?.takeIf { isReblog },
                    originalInReplyTo = entry.inReplyTo?.takeIf { isReply },
                    maxBodyLines = maxBodyLines,
                    maxTitleLines = maxTitleLines,
                    options = options,
                    optionsMenuOpen = optionsMenuOpen,
                    pollEnabled = pollEnabled,
                    reshareAndReplyVisible = reshareAndReplyVisible,
                    onBookmark = onBookmark,
                    onClick = onClick,
                    onFavorite = onFavorite,
                    onDislike = onDislike,
                    onOpenImage = onOpenImage,
                    onOpenUrl = onOpenUrl,
                    onOpenUser = onOpenUser,
                    onOpenUsersFavorite = onOpenUsersFavorite,
                    onOpenUsersReblog = onOpenUsersReblog,
                    onOptionSelected = onOptionSelected,
                    onOptionsMenuToggled = {
                        optionsMenuOpen = it
                    },
                    onPollVote = onPollVote,
                    onReblog = onReblog,
                    onReply = onReply,
                )
        }
    }
}
