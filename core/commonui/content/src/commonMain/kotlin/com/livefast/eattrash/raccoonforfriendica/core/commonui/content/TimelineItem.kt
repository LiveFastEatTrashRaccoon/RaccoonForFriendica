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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onClick?.invoke(entryToDisplay)
                }.semantics(mergeDescendants = true) {
                    val helperActions = mutableListOf<CustomAccessibilityAction>()
                    if (actionsEnabled) {
                        helperActions +=
                            CustomAccessibilityAction(
                                label = replyActionLabel,
                                action = {
                                    onReply?.invoke(entryToDisplay)
                                    true
                                },
                            )
                        helperActions +=
                            CustomAccessibilityAction(
                                label = reblogActionLabel,
                                action = {
                                    onReblog?.invoke(entryToDisplay)
                                    true
                                },
                            )
                        helperActions +=
                            CustomAccessibilityAction(
                                label = favoriteActionLabel,
                                action = {
                                    onFavorite?.invoke(entryToDisplay)
                                    true
                                },
                            )
                        helperActions +=
                            CustomAccessibilityAction(
                                label = bookmarkActionLabel,
                                action = {
                                    onBookmark?.invoke(entryToDisplay)
                                    true
                                },
                            )
                    }
                    if (reshareAndReplyVisible && isReblog) {
                        helperActions +=
                            CustomAccessibilityAction(
                                label = rebloggedActionLabel,
                                action = {
                                    entry.creator?.let { onOpenUser?.invoke(it) }
                                    true
                                },
                            )
                    }
                    if (reshareAndReplyVisible && isReply) {
                        helperActions +=
                            CustomAccessibilityAction(
                                label = inReplyToActionLabel,
                                action = {
                                    entry.inReplyTo?.creator?.let { onOpenUser?.invoke(it) }
                                    true
                                },
                            )
                    }
                    helperActions +=
                        CustomAccessibilityAction(
                            label = userActionLabel,
                            action = {
                                entryToDisplay.creator?.let { onOpenUser?.invoke(it) }
                                true
                            },
                        )
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
                    onOpenUrl = onOpenUrl,
                    onOpenUser = onOpenUser,
                    onOptionSelected = onOptionSelected,
                    onOptionsMenuToggled = {
                        optionsMenuOpen = it
                    },
                    onReblog = onReblog,
                    onReply = onReply,
                )
        }
    }
}
