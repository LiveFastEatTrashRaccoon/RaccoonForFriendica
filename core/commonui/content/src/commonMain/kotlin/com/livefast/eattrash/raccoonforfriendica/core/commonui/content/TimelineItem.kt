package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

val TimelineEntryModel.safeKey: String
    get() {
        fun StringBuilder.appendKeys(e: TimelineEntryModel) {
            append(e.id)
            append("-")
            append(e.favorite)
            append("-")
            append(e.favoriteLoading)
            append("-")
            append(e.reblogged)
            append("-")
            append(e.reblogLoading)
            append("-")
            append(e.bookmarked)
            append("-")
            append(e.bookmarkLoading)
        }
        return buildString {
            appendKeys(this@safeKey)
            reblog?.run {
                append("--")
                appendKeys(this@run)
            }
        }
    }

@Composable
fun TimelineItem(
    entry: TimelineEntryModel,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
    extendedSocialInfoEnabled: Boolean = false,
    onOpenUrl: ((String) -> Unit)? = null,
    onClick: ((TimelineEntryModel) -> Unit)? = null,
    onOpenUser: ((UserModel) -> Unit)? = null,
    onReply: ((TimelineEntryModel) -> Unit)? = null,
    onReblog: ((TimelineEntryModel) -> Unit)? = null,
    onFavorite: ((TimelineEntryModel) -> Unit)? = null,
    onBookmark: ((TimelineEntryModel) -> Unit)? = null,
    onOpenUsersFavorite: ((TimelineEntryModel) -> Unit)? = null,
    onOpenUsersReblog: ((TimelineEntryModel) -> Unit)? = null,
) {
    val isReblog = entry.reblog != null
    val entryToDisplay = entry.reblog ?: entry

    Box(
        modifier =
            modifier
                .clickable {
                    onClick?.invoke(entryToDisplay)
                }.padding(horizontal = 10.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            if (isReblog) {
                entry.creator?.let {
                    ReblogInfo(
                        modifier = Modifier.fillMaxWidth(),
                        user = it,
                        onOpenUser = onOpenUser,
                    )
                }
            } else {
                entry.inReplyTo?.creator?.let {
                    InReplyToInfo(
                        modifier = Modifier.fillMaxWidth(),
                        user = it,
                        onOpenUser = onOpenUser,
                    )
                }
            }

            ContentHeader(
                modifier = Modifier.fillMaxWidth(),
                user = entryToDisplay.creator,
                date = entry.edited ?: entry.created,
                isEdited = entry.edited != null,
                onOpenUser = onOpenUser,
            )

            entryToDisplay.title?.let { title ->
                ContentTitle(
                    content = title,
                    onClick = { onClick?.invoke(entryToDisplay) },
                    onOpenUrl = onOpenUrl,
                )
            }

            ContentBody(
                content = entryToDisplay.content,
                onClick = { onClick?.invoke(entryToDisplay) },
                onOpenUrl = onOpenUrl,
            )

            val imageUrl =
                entryToDisplay.attachments
                    .firstOrNull { attachment -> attachment.type == MediaType.Image }
                    ?.url
                    ?.takeIf { it.isNotBlank() }
            if (imageUrl != null) {
                ContentImage(
                    url = imageUrl,
                    blurred = entryToDisplay.sensitive,
                )
            }

            if (extendedSocialInfoEnabled) {
                ContentExtendedSocialInfo(
                    reblogCount = entryToDisplay.reblogCount,
                    favoriteCount = entryToDisplay.favoriteCount,
                    modifier = Modifier.padding(vertical = Spacing.xs),
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
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
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
