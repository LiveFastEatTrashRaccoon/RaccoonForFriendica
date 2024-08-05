package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

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

@Composable
fun TimelineItem(
    entry: TimelineEntryModel,
    modifier: Modifier = Modifier,
    onOpenUrl: ((String) -> Unit)? = null,
    onReply: (() -> Unit)? = null,
    onReblog: (() -> Unit)? = null,
    onFavorite: (() -> Unit)? = null,
    onBookmark: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.padding(horizontal = 10.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            ContentHeader(
                modifier = Modifier.fillMaxWidth(),
                account = entry.creator,
            )
            ContentBody(
                content = entry.content,
                onOpenUrl = onOpenUrl,
            )
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
