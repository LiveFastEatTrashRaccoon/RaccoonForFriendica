package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel

interface GetTranslationUseCase {
    suspend operator fun invoke(entry: TimelineEntryModel, targetLang: String): TranslatedTimelineEntryModel?
}
