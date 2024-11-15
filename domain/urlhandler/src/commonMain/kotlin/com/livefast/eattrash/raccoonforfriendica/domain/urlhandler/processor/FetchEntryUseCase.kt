package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

internal interface FetchEntryUseCase {
    suspend operator fun invoke(url: String): TimelineEntryModel?
}
