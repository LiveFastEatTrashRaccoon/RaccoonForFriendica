package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.contentToDisplay
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.pollToDisplay
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.spoilerToDisplay
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.titleToDisplay

@Composable
internal fun CompactTimelineItem(
    entry: TimelineEntryModel,
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
    followedHashtagsVisible: Boolean = true,
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
    onShowOriginal: (() -> Unit)? = null,
) {
    val contentHorizontalPadding = Spacing.s
    val spoiler = entry.spoilerToDisplay.orEmpty()

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
            if (followedHashtagsVisible) {
                val followedHashtags = entry.tags.filter { it.following == true }
                if (followedHashtags.isNotEmpty()) {
                    FollowedHashtagsInfo(
                        modifier = Modifier.padding(horizontal = contentHorizontalPadding),
                        tags = followedHashtags,
                        onOpenTag = { tag ->
                            tag.url?.also { onOpenUrl?.invoke(it, true) }
                        },
                    )
                }
            }
        }

        // header
        Row(
            modifier = Modifier.padding(horizontal = contentHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ContentHeader(
                modifier = Modifier.weight(1f),
                user = entry.creator,
                autoloadImages = autoloadImages,
                date = entry.updated ?: entry.created,
                scheduleDate = entry.scheduled,
                platform = entry.sourcePlatform,
                isEdited = entry.updated != null,
                onOpenUser = onOpenUser,
            )
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
            val attachments = entry.attachmentsToDisplayWithoutInlineImages
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
                        val title = entry.titleToDisplay
                        if (!title.isNullOrBlank()) {
                            ContentTitle(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .semantics { heading() },
                                content = title,
                                maxLines = maxTitleLines,
                                autoloadImages = autoloadImages,
                                emojis = entry.emojis,
                                onClick = { onClick?.invoke(entry) },
                                onOpenUrl = onOpenUrl?.let { block -> { url -> block(url, true) } },
                            )
                        }

                        // post body
                        val body = entry.contentToDisplay
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
                                emojis = entry.emojis,
                                onClick = { onClick?.invoke(entry) },
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
                            attachments = visualAttachments,
                            blurNsfw = blurNsfw,
                            autoloadImages = autoloadImages,
                            sensitive = entry.sensitive,
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
                entry.pollToDisplay?.also { poll ->
                    PollCard(
                        modifier = Modifier.fillMaxWidth(),
                        poll = poll,
                        emojis = entry.emojis,
                        enabled = pollEnabled,
                        onVote = { choices ->
                            onPollVote?.invoke(entry, choices)
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
                reblogCount = entry.reblogCount,
                favoriteCount = entry.favoriteCount,
                onOpenUsersReblog = {
                    onOpenUsersReblog?.invoke(entry)
                },
                onOpenUsersFavorite = {
                    onOpenUsersFavorite?.invoke(entry)
                },
            )
        }

        TranslationFooter(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Spacing.xs,
                        end = Spacing.m,
                    ),
            lang = entry.lang,
            isShowingTranslation = entry.isShowingTranslation,
            provider = entry.translationProvider,
            translationLoading = entry.translationLoading,
            onShowOriginal = onShowOriginal,
        )

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
                favoriteCount = entry.favoriteCount,
                favorite = entry.favorite,
                favoriteLoading = entry.favoriteLoading,
                reblogCount = entry.reblogCount,
                reblogged = entry.reblogged,
                reblogLoading = entry.reblogLoading,
                bookmarked = entry.bookmarked,
                bookmarkLoading = entry.bookmarkLoading,
                replyCount = entry.replyCount,
                disliked = entry.disliked,
                dislikeCount = entry.dislikesCount,
                dislikeLoading = entry.dislikeLoading,
                options = options,
                optionsMenuOpen = optionsMenuOpen,
                onOptionSelected = onOptionSelected,
                onOptionsMenuToggled = onOptionsMenuToggled,
                onReply =
                    if (onReply != null) {
                        { onReply(entry) }
                    } else {
                        null
                    },
                onReblog =
                    if (onReblog != null) {
                        { onReblog(entry) }
                    } else {
                        null
                    },
                onFavorite =
                    if (onFavorite != null) {
                        { onFavorite(entry) }
                    } else {
                        null
                    },
                onBookmark =
                    if (onBookmark != null) {
                        { onBookmark(entry) }
                    } else {
                        null
                    },
                onDislike =
                    if (onDislike != null) {
                        { onDislike(entry) }
                    } else {
                        null
                    },
            )
        }
    }
}

private const val COMPACT_POST_TITLE_WEIGHT = 0.8f
