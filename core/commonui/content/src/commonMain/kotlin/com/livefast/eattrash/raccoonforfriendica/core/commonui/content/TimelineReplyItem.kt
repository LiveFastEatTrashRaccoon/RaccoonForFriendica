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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.getThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original

@Composable
fun TimelineReplyItem(
    entry: TimelineEntryModel,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
    blurNsfw: Boolean = true,
    autoloadImages: Boolean = true,
    layout: TimelineLayout = TimelineLayout.Full,
    options: List<Option> = emptyList(),
    onOpenUrl: ((String) -> Unit)? = null,
    onClick: ((TimelineEntryModel) -> Unit)? = null,
    onOpenUser: ((UserModel) -> Unit)? = null,
    onReply: ((TimelineEntryModel) -> Unit)? = null,
    onReblog: ((TimelineEntryModel) -> Unit)? = null,
    onFavorite: ((TimelineEntryModel) -> Unit)? = null,
    onBookmark: ((TimelineEntryModel) -> Unit)? = null,
    onOpenImage: ((List<String>, Int, List<Int>) -> Unit)? = null,
    onOptionSelected: ((OptionId) -> Unit)? = null,
) {
    val entryToDisplay = entry.original
    val depthZeroBased = entry.depth - 1
    val themeRepository = remember { getThemeRepository() }
    val barWidth = 3.dp
    val barColor = themeRepository.getCommentBarColor(depthZeroBased)
    var barHeight by remember { mutableStateOf(0.dp) }
    val indentAmount = Spacing.s + (barWidth + Spacing.s) * depthZeroBased
    val localDensity = LocalDensity.current
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
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
                    val helperActions: MutableList<CustomAccessibilityAction> = mutableListOf()
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
                TimelineLayout.Full ->
                    FullTimelineItem(
                        modifier = contentModifier,
                        actionsEnabled = actionsEnabled,
                        autoloadImages = autoloadImages,
                        blurNsfw = blurNsfw,
                        entryToDisplay = entryToDisplay,
                        options = options,
                        optionsMenuOpen = optionsMenuOpen,
                        onBookmark = onBookmark,
                        onClick = onClick,
                        onFavorite = onFavorite,
                        onOpenImage = onOpenImage,
                        onOpenUrl = onOpenUrl,
                        onOpenUser = onOpenUser,
                        onOptionSelected = onOptionSelected,
                        onOptionsMenuToggled = {
                            optionsMenuOpen = it
                        },
                        onReblog = onReblog,
                        onReply = onReply,
                    )

                TimelineLayout.DistractionFree ->
                    DistractionFreeTimelineItem(
                        modifier = contentModifier,
                        actionsEnabled = actionsEnabled,
                        autoloadImages = autoloadImages,
                        entryToDisplay = entryToDisplay,
                        options = options,
                        optionsMenuOpen = optionsMenuOpen,
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
}
