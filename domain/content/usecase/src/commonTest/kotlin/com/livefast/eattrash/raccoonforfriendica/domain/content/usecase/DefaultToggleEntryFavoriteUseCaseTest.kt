package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import dev.mokkery.mock

class DefaultToggleEntryFavoriteUseCaseTest {
    private val entryRepository = mock<TimelineEntryRepository>()
    private val sut = DefaultToggleEntryFavoriteUseCase(entryRepository)
}
