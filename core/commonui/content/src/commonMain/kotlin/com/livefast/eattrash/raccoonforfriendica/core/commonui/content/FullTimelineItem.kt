package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.QuoteStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.cardToDisplay
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.contentToDisplay
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.pollToDisplay
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.spoilerToDisplay
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.titleToDisplay

@Composable
internal fun FullTimelineItem(
    post: PostModel,
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
    originalInReplyTo: PostModel? = null,
    pollEnabled: Boolean = true,
    reshareAndReplyVisible: Boolean = true,
    followedHashtagsVisible: Boolean = true,
    onBookmark: ((PostModel) -> Unit)? = null,
    onClick: ((PostModel) -> Unit)? = null,
    onFavorite: ((PostModel) -> Unit)? = null,
    onDislike: ((PostModel) -> Unit)? = null,
    onOpenImage: ((List<String>, Int, List<Int>) -> Unit)? = null,
    onOpenUrl: ((String, Boolean) -> Unit)? = null,
    onOpenUser: ((UserModel) -> Unit)? = null,
    onOpenPostsQuoting: ((PostModel) -> Unit)? = null,
    onOpenUsersFavorite: ((PostModel) -> Unit)? = null,
    onOpenUsersReblog: ((PostModel) -> Unit)? = null,
    onSelectOption: ((OptionId) -> Unit)? = null,
    onToggleOptionsMenu: ((Boolean) -> Unit)? = null,
    onPollVote: ((PostModel, List<Int>) -> Unit)? = null,
    onReblog: ((PostModel) -> Unit)? = null,
    onReply: ((PostModel) -> Unit)? = null,
    onShowOriginal: (() -> Unit)? = null,
    onOpenQuote: ((PostModel) -> Unit)? = null,
) {
    val contentHorizontalPadding = Spacing.s
    val spoiler = post.spoilerToDisplay.orEmpty()
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

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
                val followedHashtags = post.tags.filter { it.following == true }
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
                user = post.creator,
                autoloadImages = autoloadImages,
                date = post.updated ?: post.created,
                scheduleDate = post.scheduled,
                platform = post.sourcePlatform,
                isEdited = post.updated != null,
                onOpenUser = onOpenUser,
            )
            if (options.isNotEmpty() && !actionsEnabled) {
                var optionsOffset by remember { mutableStateOf(Offset.Zero) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
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
                                imageVector = LocalResources.current.moreVert,
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
                val title = post.titleToDisplay
                if (!title.isNullOrBlank()) {
                    ContentTitle(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .semantics { heading() },
                        content = title,
                        maxLines = maxTitleLines,
                        autoloadImages = autoloadImages,
                        emojis = post.emojis,
                        onClick = { onClick?.invoke(post) },
                        onOpenUrl = onOpenUrl?.let { block -> { url -> block(url, true) } },
                    )
                }

                // post body
                val body = post.contentToDisplay
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
                        hideInlineQuotes = post.quoted != null,
                        maxLines = maxBodyLines,
                        emojis = post.emojis,
                        onClick = { onClick?.invoke(post) },
                        onOpenUrl = onOpenUrl?.let { block -> { url -> block(url, true) } },
                    )
                }

                // attachments
                val attachments = post.attachmentsToDisplayWithoutInlineImages
                val visualAttachments =
                    attachments.filter { it.type == MediaType.Image || it.type == MediaType.Video }
                val audioAttachments = attachments.filter { it.type == MediaType.Audio }
                if (visualAttachments.isNotEmpty()) {
                    ContentVisualAttachments(
                        modifier =
                        Modifier.fillMaxWidth().padding(
                            top = Spacing.s,
                            bottom = Spacing.xxxs,
                        ),
                        attachments = visualAttachments,
                        blurNsfw = blurNsfw,
                        autoloadImages = autoloadImages,
                        sensitive = post.sensitive,
                        onOpenImage = onOpenImage,
                    )
                }
                if (audioAttachments.isNotEmpty()) {
                    ContentAudioAttachments(
                        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xxxs),
                        attachments = audioAttachments,
                    )
                }

                // poll
                post.pollToDisplay?.also { poll ->
                    PollCard(
                        modifier = Modifier.fillMaxWidth(),
                        poll = poll,
                        emojis = post.emojis,
                        enabled = pollEnabled,
                        onVote = { choices ->
                            onPollVote?.invoke(post, choices)
                        },
                    )
                }

                // preview
                post.cardToDisplay?.also { preview ->
                    ContentPreview(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.s),
                        card =
                        preview.copy(
                            title =
                            preview.title
                                .takeIf { !post.content.startsWith(it) }
                                .orEmpty(),
                            image = preview.image.takeIf { attachments.isEmpty() },
                        ),
                        autoloadImages = autoloadImages,
                        onOpen = onOpenUrl?.let { block -> { url -> block(url, false) } },
                        onOpenImage = { url ->
                            onOpenImage?.invoke(listOf(url), 0, emptyList())
                        },
                    )
                }

                // quoted post
                post.quoted?.takeIf { it.quoteStatus == QuoteStatus.Accepted }?.also { post ->
                    FullTimelineItem(
                        modifier = Modifier
                            .padding(top = Spacing.xs)
                            .fillMaxWidth()
                            .border(
                                width = Dp.Hairline,
                                color = ancillaryColor,
                                shape = RoundedCornerShape(CornerSize.xl),
                            ).padding(vertical = Spacing.xxs)
                            .clickable {
                                onOpenQuote?.invoke(post)
                            },
                        post = post,
                        actionsEnabled = false,
                        autoloadImages = autoloadImages,
                        blurNsfw = blurNsfw,
                        reshareAndReplyVisible = false,
                        followedHashtagsVisible = false,
                        pollEnabled = false,
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
                reblogCount = post.reblogCount,
                favoriteCount = post.favoriteCount,
                quoteCount = post.quoteCount,
                onOpenPostsQuoting = {
                    onOpenPostsQuoting?.invoke(post)
                },
                onOpenUsersReblog = {
                    onOpenUsersReblog?.invoke(post)
                },
                onOpenUsersFavorite = {
                    onOpenUsersFavorite?.invoke(post)
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
            lang = post.lang,
            isShowingTranslation = post.isShowingTranslation,
            provider = post.translationProvider,
            translationLoading = post.translationLoading,
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
                favoriteCount = post.favoriteCount,
                favorite = post.favorite,
                favoriteLoading = post.favoriteLoading,
                reblogCount = post.reblogCount,
                reblogged = post.reblogged,
                reblogLoading = post.reblogLoading,
                bookmarked = post.bookmarked,
                bookmarkLoading = post.bookmarkLoading,
                replyCount = post.replyCount,
                disliked = post.disliked,
                dislikeCount = post.dislikesCount,
                dislikeLoading = post.dislikeLoading,
                options = options,
                optionsMenuOpen = optionsMenuOpen,
                onSelectOption = onSelectOption,
                onToggleOptionsMenu = onToggleOptionsMenu,
                onReply =
                if (onReply != null) {
                    { onReply(post) }
                } else {
                    null
                },
                onReblog =
                if (onReblog != null) {
                    { onReblog(post) }
                } else {
                    null
                },
                onFavorite =
                if (onFavorite != null) {
                    { onFavorite(post) }
                } else {
                    null
                },
                onBookmark =
                if (onBookmark != null) {
                    { onBookmark(post) }
                } else {
                    null
                },
                onDislike =
                if (onDislike != null) {
                    { onDislike(post) }
                } else {
                    null
                },
            )
        } else {
            Spacer(Modifier.height(Spacing.xxs))
        }
    }
}
