package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface GetInnerUrlUseCase {
    suspend operator fun invoke(entry: TimelineEntryModel): String?
}
