package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface EntryActionHelper {
    fun TimelineEntryModel.withActions(): TimelineEntryModel
}
