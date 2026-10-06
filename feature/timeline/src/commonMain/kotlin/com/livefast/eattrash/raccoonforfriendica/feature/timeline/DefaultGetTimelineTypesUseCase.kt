package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart

@OptIn(ExperimentalCoroutinesApi::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultGetTimelineTypesUseCase(
    private val circlesRepository: CirclesRepository,
    private val identityRepository: IdentityRepository,
) : GetTimelineTypesUseCase {
    private val refreshSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override operator fun invoke(): Flow<List<TimelineType>> = combine(
        refreshSignal.onStart { emit(Unit) },
        identityRepository.currentUser,
    ) { _, user ->
        user
    }.mapLatest { user ->
        val circles = circlesRepository.getAll().orEmpty()
        val isLogged = user != null
        val defaultTimelineTypes = buildList {
            this += TimelineType.All
            if (isLogged) {
                this += TimelineType.Subscriptions
            }
            this += TimelineType.Local
        }
        defaultTimelineTypes + circles.map { TimelineType.Circle(circle = it) }
    }

    override suspend fun refresh() {
        refreshSignal.emit(Unit)
    }
}
