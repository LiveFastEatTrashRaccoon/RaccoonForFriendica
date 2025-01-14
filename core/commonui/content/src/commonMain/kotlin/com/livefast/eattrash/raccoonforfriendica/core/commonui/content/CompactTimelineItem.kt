package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.embeddedImageUrls

@Composable
internal fun CompactTimelineItem(
    entryToDisplay: TimelineEntryModel,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
    autoloadImages: Boolean = true,
    blurNsfw: Boolean = true,
    extendedSocialInfoEnabled: Boolean = false,
    maxBodyLines: Int = Int.MAX_VALUE,
    maxTitleLines: Int = Int.MAX_VALUE,
    options: List<Option> = emptyList(),
    optionsMenuOpen: Boolean = false,
    originalCreator: UserModel? = null,
    originalInReplyTo: TimelineEntryModel? = null,
    pollEnabled: Boolean = true,
    reshareAndReplyVisible: Boolean = true,
    onBookmark: ((TimelineEntryModel) -> Unit)? = null,
    onClick: ((TimelineEntryModel) -> Unit)? = null,
    onFavorite: ((TimelineEntryModel) -> Unit)? = null,
    onDislike: ((TimelineEntryModel) -> Unit)? = null,
    onOpenImage: ((List<String>, Int, List<Int>) -> Unit)? = null,
    onOpenUrl: ((String, Boolean) -> Unit)? = null,
    onOpenUser: ((UserModel) -> Unit)? = null,
    onOpenUsersFavorite: ((TimelineEntryModel) -> Unit)? = null,
    onOpenUsersReblog: ((TimelineEntryModel) -> Unit)? = null,
    onOptionSelected: ((OptionId) -> Unit)? = null,
    onOptionsMenuToggled: ((Boolean) -> Unit)? = null,
    onPollVote: ((TimelineEntryModel, List<Int>) -> Unit)? = null,
    onReblog: ((TimelineEntryModel) -> Unit)? = null,
    onReply: ((TimelineEntryModel) -> Unit)? = null,
) {
    val contentHorizontalPadding = Spacing.s
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
    val spoiler = entryToDisplay.spoiler.orEmpty()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        // reshare and reply info
        if (reshareAndReplyVisible) {
            Column(
                modifier = Modifier.padding(horizontal = contentHorizontalPadding),
            ) {
                originalCreator?.let {
                    ReblogInfo(
                        user = it,
                        autoloadImages = autoloadImages,
                        onOpenUser = onOpenUser,
                    )
                }
                originalInReplyTo?.creator?.let {
                    InReplyToInfo(
                        user = it,
                        autoloadImages = autoloadImages,
                        onOpenUser = onOpenUser,
                    )
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
                            Modifier
                                .onGloballyPositioned {
                                    optionsOffset = it.positionInParent()
                                }.clearAndSetSemantics { },
                        onClick = {
                            onOptionsMenuToggled?.invoke(true)
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
                            onOptionsMenuToggled?.invoke(false)
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
                                    onOptionsMenuToggled?.invoke(false)
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
            val attachments =
                entryToDisplay.attachments.filter { it.url !in entryToDisplay.embeddedImageUrls }
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Row(
                    modifier = Modifier.padding(vertical = Spacing.xxxs),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(COMPACT_POST_TITLE_WEIGHT),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        // post title
                        val title = entryToDisplay.title
                        if (!title.isNullOrBlank()) {
                            ContentTitle(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .semantics { heading() },
                                content = title,
                                maxLines = maxTitleLines,
                                autoloadImages = autoloadImages,
                                emojis = entryToDisplay.emojis,
                                onClick = { onClick?.invoke(entryToDisplay) },
                                onOpenUrl = onOpenUrl?.let { block -> { url -> block(url, true) } },
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
                                onOpenUrl = onOpenUrl?.let { block -> { url -> block(url, true) } },
                            )
                        }
                    }

                    // visual attachments
                    val visualAttachments =
                        attachments.filter { it.type == MediaType.Image || it.type == MediaType.Video }
                    if (visualAttachments.isNotEmpty()) {
                        ContentVisualAttachments(
                            modifier = Modifier.weight(1 - COMPACT_POST_TITLE_WEIGHT),
                            attachments =
                                entryToDisplay.attachments.filter {
                                    it.url !in entryToDisplay.embeddedImageUrls
                                },
                            blurNsfw = blurNsfw,
                            autoloadImages = autoloadImages,
                            sensitive = entryToDisplay.sensitive,
                            cornerSize = CornerSize.s,
                            onOpenImage = onOpenImage,
                        )
                    }
                }
                // audio attachments
                val audioAttachments = attachments.filter { it.type == MediaType.Audio }
                if (audioAttachments.isNotEmpty()) {
                    ContentAudioAttachments(
                        modifier =
                            Modifier.fillMaxWidth().padding(vertical = Spacing.xxxs),
                        attachments = audioAttachments,
                    )
                }

                // poll
                entryToDisplay.poll?.also { poll ->
                    PollCard(
                        modifier = Modifier.fillMaxWidth(),
                        poll = poll,
                        emojis = entryToDisplay.emojis,
                        enabled = pollEnabled,
                        onVote = { choices ->
                            onPollVote?.invoke(entryToDisplay, choices)
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
                disliked = entryToDisplay.disliked,
                dislikeCount = entryToDisplay.dislikesCount,
                dislikeLoading = entryToDisplay.dislikeLoading,
                onReply =
                    if (onReply != null) {
                        { onReply(entryToDisplay) }
                    } else {
                        null
                    },
                onReblog =
                    if (onReblog != null) {
                        { onReblog(entryToDisplay) }
                    } else {
                        null
                    },
                onFavorite =
                    if (onFavorite != null) {
                        { onFavorite(entryToDisplay) }
                    } else {
                        null
                    },
                onBookmark =
                    if (onBookmark != null) {
                        { onBookmark(entryToDisplay) }
                    } else {
                        null
                    },
                onDislike =
                    if (onDislike != null) {
                        { onDislike(entryToDisplay) }
                    } else {
                        null
                    },
            )
        }
    }
}

private const val COMPACT_POST_TITLE_WEIGHT = 0.8f
