package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface ReplyHelper {
    suspend fun TimelineEntryModel.withInReplyToIfMissing(): TimelineEntryModel
}
