package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel

interface TranslationRepository {
    suspend fun getTranslation(entry: TimelineEntryModel, targetLang: String): TranslatedTimelineEntryModel?
}
