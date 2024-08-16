package com.livefast.eattrash.raccoonforfriendica.feature.thread.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface PopulateThreadUseCase {
    suspend operator fun invoke(entryId: String): List<TimelineEntryModel>
}
