package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface ToggleTranslationUseCase {
    suspend operator fun invoke(entry: TimelineEntryModel, targetLang: String): TimelineEntryModel
}
