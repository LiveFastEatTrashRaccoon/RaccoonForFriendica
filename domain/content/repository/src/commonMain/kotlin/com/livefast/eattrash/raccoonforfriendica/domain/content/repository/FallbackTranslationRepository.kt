package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel

interface FallbackTranslationRepository {
    suspend fun getTranslation(
        entry: TimelineEntryModel,
        targetLang: String,
        config: TranslationProviderConfig?,
    ): TranslatedTimelineEntryModel?
}
