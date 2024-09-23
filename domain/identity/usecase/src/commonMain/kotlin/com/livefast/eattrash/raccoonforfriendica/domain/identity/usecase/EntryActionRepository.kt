package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

@Stable
interface EntryActionRepository {
    fun canShare(entry: TimelineEntryModel): Boolean

    fun canReply(entry: TimelineEntryModel): Boolean

    fun canReact(entry: TimelineEntryModel): Boolean

    fun canBookmark(entry: TimelineEntryModel): Boolean

    fun canReblog(entry: TimelineEntryModel): Boolean

    fun canEdit(entry: TimelineEntryModel): Boolean

    fun canDelete(entry: TimelineEntryModel): Boolean

    fun canReport(entry: TimelineEntryModel): Boolean

    fun canMute(entry: TimelineEntryModel): Boolean

    fun canTogglePin(entry: TimelineEntryModel): Boolean

    fun canBlock(entry: TimelineEntryModel): Boolean

    fun canQuote(entry: TimelineEntryModel): Boolean
}
