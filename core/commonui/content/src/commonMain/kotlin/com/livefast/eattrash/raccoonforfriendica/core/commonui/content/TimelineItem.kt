package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
fun TimelineItem(
    entry: TimelineEntryModel,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
    pollEnabled: Boolean = true,
    extendedSocialInfoEnabled: Boolean = false,
    reshareAndReplyVisible: Boolean = true,
    blurNsfw: Boolean = true,
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
    onOpenImage: ((String) -> Unit)? = null,
    onOptionSelected: ((OptionId) -> Unit)? = null,
    onPollVote: ((TimelineEntryModel, List<Int>) -> Unit)? = null,
    onToggleSpoilerActive: ((TimelineEntryModel) -> Unit)? = null,
) {
    val isReblog = entry.reblog != null
    val isReply = entry.inReplyTo != null
    val entryToDisplay = entry.reblog ?: entry
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
    var optionsMenuOpen by remember { mutableStateOf(false) }
    val spoiler = entryToDisplay.spoiler.orEmpty()
    val contentHorizontalPadding = Spacing.s

    Column(
        modifier =
            modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onClick?.invoke(entryToDisplay)
                },
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            if (reshareAndReplyVisible) {
                Column(
                    modifier = Modifier.padding(horizontal = contentHorizontalPadding),
                ) {
                    if (isReblog) {
                        entry.creator?.let {
                            ReblogInfo(
                                user = it,
                                onOpenUser = onOpenUser,
                            )
                        }
                    }
                    if (isReply) {
                        entry.inReplyTo?.creator?.let {
                            InReplyToInfo(
                                user = it,
                                onOpenUser = onOpenUser,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = contentHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ContentHeader(
                    modifier = Modifier.weight(1f),
                    user = entryToDisplay.creator,
                    date = entryToDisplay.edited ?: entryToDisplay.created,
                    isEdited = entryToDisplay.edited != null,
                    onOpenUser = onOpenUser,
                )
                if (options.isNotEmpty()) {
                    Box {
                        IconButton(
                            modifier =
                                Modifier.onGloballyPositioned {
                                    optionsOffset = it.positionInParent()
                                },
                            onClick = {
                                optionsMenuOpen = true
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
                                optionsMenuOpen = false
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
                                        optionsMenuOpen = false
                                        onOptionSelected?.invoke(option.id)
                                    },
                                )
                            }
                        }
                    }
                }
            }

            if (spoiler.isNotEmpty()) {
                SpoilerCard(
                    modifier =
                        Modifier.fillMaxWidth().padding(
                            vertical = Spacing.s,
                            horizontal = contentHorizontalPadding,
                        ),
                    content =
                        if (entryToDisplay.isSpoilerActive) {
                            LocalStrings.current.actionHideContent
                        } else {
                            spoiler
                        },
                    onClick = {
                        onToggleSpoilerActive?.invoke(entryToDisplay)
                    },
                )
            }
            if (entryToDisplay.isSpoilerActive || spoiler.isEmpty()) {
                val title = entryToDisplay.title
                if (title != null) {
                    ContentTitle(
                        modifier =
                            Modifier.fillMaxWidth().padding(
                                start = contentHorizontalPadding,
                                end = contentHorizontalPadding,
                            ),
                        content = title,
                        onClick = { onClick?.invoke(entryToDisplay) },
                        onOpenUrl = onOpenUrl,
                    )
                }

                ContentBody(
                    modifier =
                        Modifier.fillMaxWidth().then(
                            if (title == null) {
                                Modifier.padding(
                                    start = contentHorizontalPadding,
                                    end = contentHorizontalPadding,
                                )
                            } else {
                                Modifier.padding(
                                    top = Spacing.xxxs,
                                    start = contentHorizontalPadding,
                                    end = contentHorizontalPadding,
                                )
                            },
                        ),
                    content = entryToDisplay.content,
                    onClick = { onClick?.invoke(entryToDisplay) },
                    onOpenUrl = onOpenUrl,
                )

                val firstImageAttachment =
                    entryToDisplay.attachments
                        .firstOrNull { attachment -> attachment.type == MediaType.Image }
                val imageUrl = firstImageAttachment?.url?.takeIf { it.isNotBlank() }
                if (imageUrl != null) {
                    ContentImage(
                        modifier =
                            Modifier.fillMaxWidth().padding(
                                top = Spacing.s,
                                bottom = Spacing.xxxs,
                                start = contentHorizontalPadding,
                                end = contentHorizontalPadding,
                            ),
                        url = imageUrl,
                        altText = firstImageAttachment.description,
                        blurHash = firstImageAttachment.blurHash,
                        originalWidth = firstImageAttachment.originalWidth ?: 0,
                        originalHeight = firstImageAttachment.originalHeight ?: 0,
                        sensitive = blurNsfw && entryToDisplay.sensitive,
                        onClick = { onOpenImage?.invoke(imageUrl) },
                    )
                }
                entryToDisplay.poll?.also { poll ->
                    PollCard(
                        modifier =
                            Modifier.fillMaxWidth().padding(
                                start = contentHorizontalPadding,
                                end = contentHorizontalPadding,
                            ),
                        poll = poll,
                        enabled = pollEnabled,
                        onVote = { choices ->
                            onPollVote?.invoke(entryToDisplay, choices)
                        },
                    )
                }
                entryToDisplay.card?.also { preview ->
                    ContentPreview(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = Spacing.s,
                                    start = contentHorizontalPadding,
                                    end = contentHorizontalPadding,
                                ),
                        card = preview.copy(image = preview.image.takeIf { it != imageUrl }),
                        onOpen = onOpenUrl,
                        onOpenImage = onOpenImage,
                    )
                }
            }

            if (extendedSocialInfoEnabled) {
                ContentExtendedSocialInfo(
                    modifier =
                        Modifier.padding(
                            vertical = Spacing.xs,
                            horizontal = contentHorizontalPadding,
                        ),
                    reblogCount = entryToDisplay.reblogCount,
                    favoriteCount = entryToDisplay.favoriteCount,
                    onOpenUsersReblog = {
                        onOpenUsersReblog?.invoke(entryToDisplay)
                    },
                    onOpenUsersFavorite = {
                        onOpenUsersFavorite?.invoke(entryToDisplay)
                    },
                )
            }

            if (actionsEnabled) {
                ContentFooter(
                    modifier =
                        Modifier.fillMaxWidth().padding(
                            top = Spacing.xs,
                            start = contentHorizontalPadding,
                            end = contentHorizontalPadding,
                        ),
                    favoriteCount = entryToDisplay.favoriteCount,
                    favorite = entryToDisplay.favorite,
                    favoriteLoading = entryToDisplay.favoriteLoading,
                    reblogCount = entryToDisplay.reblogCount,
                    reblogged = entryToDisplay.reblogged,
                    reblogLoading = entryToDisplay.reblogLoading,
                    bookmarked = entryToDisplay.bookmarked,
                    bookmarkLoading = entryToDisplay.bookmarkLoading,
                    replyCount = entryToDisplay.replyCount,
                    onReply = {
                        onReply?.invoke(entryToDisplay)
                    },
                    onReblog = {
                        onReblog?.invoke(entryToDisplay)
                    },
                    onFavorite = {
                        onFavorite?.invoke(entryToDisplay)
                    },
                    onBookmark = {
                        onBookmark?.invoke(entryToDisplay)
                    },
                )
            }
        }
    }
}
