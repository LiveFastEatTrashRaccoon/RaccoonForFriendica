package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface PopulateThreadUseCase {
    suspend operator fun invoke(
        entry: TimelineEntryModel,
        maxDepth: Int = 5,
    ): List<TimelineEntryModel>
}
