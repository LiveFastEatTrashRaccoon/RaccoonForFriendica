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
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
fun TimelineItem(
    entry: TimelineEntryModel,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
    onOpenUrl: ((String) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onOpenUser: ((UserModel) -> Unit)? = null,
    onReply: (() -> Unit)? = null,
    onReblog: (() -> Unit)? = null,
    onFavorite: (() -> Unit)? = null,
    onBookmark: (() -> Unit)? = null,
) {
    val isReblog = entry.reblog != null

    Box(
        modifier =
            modifier
                .clickable {
                    onClick?.invoke()
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
            }
            entry.inReplyTo?.creator?.let {
                InReplyToInfo(
                    modifier = Modifier.fillMaxWidth(),
                    user = it,
                    onOpenUser = onOpenUser,
                )
            }

            ContentHeader(
                modifier = Modifier.fillMaxWidth(),
                user =
                    if (isReblog) {
                        entry.reblog?.creator
                    } else {
                        entry.creator
                    },
                onOpenUser = onOpenUser,
            )

            entry.title?.let { title ->
                ContentTitle(
                    content = title,
                    onClick = onClick,
                    onOpenUrl = onOpenUrl,
                )
            }

            ContentBody(
                content = entry.content,
                onClick = onClick,
                onOpenUrl = onOpenUrl,
            )

            if (actionsEnabled) {
                ContentFooter(
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
                    favoriteCount = entry.favoriteCount,
                    favorite = entry.favorite,
                    reblogCount = entry.reblogCount,
                    reblogged = entry.reblogged,
                    bookmarked = entry.bookmarked,
                    replyCount = entry.replyCount,
                    onReply = onReply,
                    onReblog = onReblog,
                    onFavorite = onFavorite,
                    onBookmark = onBookmark,
                )
            }
        }
    }
}
