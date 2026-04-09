package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel

data class FallbackTranslationProviderConfig(val name: String, val url: String, val apiKey: String)

interface FallbackTranslationRepository {
    suspend fun getTranslation(
        entry: TimelineEntryModel,
        targetLang: String,
        config: FallbackTranslationProviderConfig?,
    ): TranslatedTimelineEntryModel?
}
