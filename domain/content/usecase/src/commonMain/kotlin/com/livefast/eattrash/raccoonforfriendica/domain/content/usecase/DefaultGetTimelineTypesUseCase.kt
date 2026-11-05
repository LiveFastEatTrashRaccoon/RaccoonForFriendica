package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FeedTypeRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultGetTimelineTypesUseCase(
    private val accountRepository: AccountRepository,
    private val feedTypeRepository: FeedTypeRepository,
    private val circlesRepository: CirclesRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GetTimelineTypesUseCase {

    private val refreshSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val data = MutableStateFlow<List<TimelineType>>(emptyList())

    init {
        scope.launch {
            accountRepository.getActiveAsFlow()
                .flatMapLatest { account ->
                    val feedFlow = if (account != null) {
                        feedTypeRepository.getAllAsFlow(account.id)
                    } else {
                        flowOf()
                    }
                    combine(
                        feedFlow,
                        refreshSignal.onStart { emit(Unit) },
                    ) { feedTypes, _ ->
                        feedTypes
                    }
                }.map { feedTypes ->
                    // on Friendica, circle data are different when returned from getAll() and from get(String):
                    // only in the former IDs have "group:" / "channel:" prefixes and readable names
                    val allCircles = circlesRepository.getAll().orEmpty()
                    feedTypes.mapNotNull { feedType ->
                        when (val timelineType = feedType.type) {
                            is TimelineType.Circle -> {
                                val circleId = feedType.info.orEmpty()
                                val circle = allCircles.firstOrNull { it.id == circleId } ?: return@mapNotNull null
                                timelineType.copy(circle = circle)
                            }

                            else -> timelineType
                        }
                    }
                }.onEach { types ->
                    data.update { types }
                }.launchIn(this)
        }
    }

    override operator fun invoke(): Flow<List<TimelineType>> = data

    override suspend fun refresh() {
        refreshSignal.emit(Unit)
    }
}
