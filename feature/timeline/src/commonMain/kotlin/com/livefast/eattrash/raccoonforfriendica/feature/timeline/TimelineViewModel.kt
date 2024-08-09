package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TimelineViewModel(
    private val paginationManager: TimelinePaginationManager,
    private val apiConfigurationRepository: ApiConfigurationRepository,
) : DefaultMviModel<TimelineMviModel.Intent, TimelineMviModel.State, TimelineMviModel.Effect>(
        initialState = TimelineMviModel.State(),
    ),
    TimelineMviModel {
    init {
        screenModelScope.launch {
            apiConfigurationRepository.node
                .onEach { node ->
                    updateState { it.copy(nodeName = node) }
                }.launchIn(this)

            apiConfigurationRepository.isLogged
                .onEach { isLogged ->
                    if (isLogged) {
                        updateState {
                            it.copy(
                                availableTimelineTypes =
                                    listOf(
                                        TimelineType.All,
                                        TimelineType.Subscriptions,
                                        TimelineType.Local,
                                    ),
                                timelineType = TimelineType.Subscriptions,
                            )
                        }
                        refresh(initial = true)
                    } else {
                        updateState {
                            it.copy(
                                availableTimelineTypes =
                                    listOf(
                                        TimelineType.All,
                                        TimelineType.Local,
                                    ),
                                timelineType = TimelineType.Local,
                            )
                        }
                        refresh(initial = true)
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: TimelineMviModel.Intent) {
        when (intent) {
            TimelineMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            TimelineMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            is TimelineMviModel.Intent.ChangeType ->
                screenModelScope.launch {
                    if (uiState.value.loading) {
                        return@launch
                    }

                    updateState {
                        it.copy(timelineType = intent.type)
                    }
                    emitEffect(TimelineMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            TimelinePaginationSpecification.Feed(
                timelineType = uiState.value.timelineType,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val entries = paginationManager.loadNextPage()
        updateState {
            it.copy(
                entries = entries,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
    }
}
