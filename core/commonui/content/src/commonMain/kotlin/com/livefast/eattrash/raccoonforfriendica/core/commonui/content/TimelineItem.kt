package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.animation.AnimatedVisibility
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
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.embeddedImageUrls
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
            // reshare and reply info
            if (reshareAndReplyVisible) {
                Column(
                    modifier = Modifier.padding(horizontal = contentHorizontalPadding),
                ) {
                    if (isReblog) {
                        entry.creator?.let {
                            ReblogInfo(
                                user = it,
                                autoloadImages = autoloadImages,
                                onOpenUser = onOpenUser,
                            )
                        }
                    }
                    if (isReply) {
                        entry.inReplyTo?.creator?.let {
                            InReplyToInfo(
                                user = it,
                                autoloadImages = autoloadImages,
                                onOpenUser = onOpenUser,
                            )
                        }
                    }
                }
            }

            // header and options
            Row(
                modifier = Modifier.padding(horizontal = contentHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ContentHeader(
                    modifier = Modifier.weight(1f),
                    user = entryToDisplay.creator,
                    autoloadImages = autoloadImages,
                    date = entryToDisplay.updated ?: entryToDisplay.created,
                    scheduleDate = entryToDisplay.scheduled,
                    isEdited = entryToDisplay.updated != null,
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

            // post spoiler
            var spoilerActive by remember { mutableStateOf(false) }
            if (spoiler.isNotEmpty()) {
                SpoilerCard(
                    modifier =
                        Modifier.fillMaxWidth().padding(
                            vertical = Spacing.s,
                            horizontal = contentHorizontalPadding,
                        ),
                    content = spoiler,
                    autoloadImages = autoloadImages,
                    active = spoilerActive,
                    onClick = {
                        spoilerActive = !spoilerActive
                    },
                )
            }

            AnimatedVisibility(
                modifier = Modifier.padding(horizontal = contentHorizontalPadding),
                visible = spoilerActive || spoiler.isEmpty(),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    // post title
                    val title = entryToDisplay.title
                    if (!title.isNullOrBlank()) {
                        ContentTitle(
                            modifier = Modifier.fillMaxWidth(),
                            content = title,
                            maxLines = maxTitleLines,
                            autoloadImages = autoloadImages,
                            emojis = entryToDisplay.emojis,
                            onClick = { onClick?.invoke(entryToDisplay) },
                            onOpenUrl = onOpenUrl,
                        )
                    }

                    // post body
                    val body = entryToDisplay.content
                    if (body.isNotBlank()) {
                        ContentBody(
                            modifier =
                                Modifier.fillMaxWidth().then(
                                    if (title == null) {
                                        Modifier
                                    } else {
                                        Modifier.padding(top = Spacing.xxxs)
                                    },
                                ),
                            content = body,
                            autoloadImages = autoloadImages,
                            maxLines = maxBodyLines,
                            emojis = entryToDisplay.emojis,
                            onClick = { onClick?.invoke(entryToDisplay) },
                            onOpenUrl = onOpenUrl,
                        )
                    }

                    // attachments
                    ContentAttachments(
                        modifier =
                            Modifier.fillMaxWidth().padding(
                                top = Spacing.s,
                                bottom = Spacing.xxxs,
                            ),
                        attachments =
                            entryToDisplay.attachments.filter {
                                it.url !in entryToDisplay.embeddedImageUrls
                            },
                        blurNsfw = blurNsfw,
                        autoloadImages = autoloadImages,
                        sensitive = entryToDisplay.sensitive,
                        onOpenImage = onOpenImage,
                    )

                    // poll
                    entryToDisplay.poll?.also { poll ->
                        PollCard(
                            modifier = Modifier.fillMaxWidth(),
                            poll = poll,
                            enabled = pollEnabled,
                            onVote = { choices ->
                                onPollVote?.invoke(entryToDisplay, choices)
                            },
                        )
                    }

                    // preview
                    entryToDisplay.card?.also { preview ->
                        val attachments = entryToDisplay.attachments
                        ContentPreview(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = Spacing.s),
                            card =
                                preview.copy(
                                    title =
                                        preview.title
                                            .takeIf { !entryToDisplay.content.startsWith(it) }
                                            .orEmpty(),
                                    image = preview.image.takeIf { attachments.isEmpty() },
                                ),
                            autoloadImages = autoloadImages,
                            onOpen = onOpenUrl,
                            onOpenImage = { url ->
                                onOpenImage?.invoke(listOf(url), 0, emptyList())
                            },
                        )
                    }
                }
            }

            // reblog and favorite info
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
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                top = Spacing.xxs,
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
