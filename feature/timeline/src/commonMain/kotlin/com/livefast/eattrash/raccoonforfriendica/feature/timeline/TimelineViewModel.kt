package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toTimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TimelineViewModel(
    private val paginationManager: TimelinePaginationManager,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val settingsRepository: SettingsRepository,
) : DefaultMviModel<TimelineMviModel.Intent, TimelineMviModel.State, TimelineMviModel.Effect>(
        initialState = TimelineMviModel.State(),
    ),
    TimelineMviModel {
    init {
        screenModelScope.launch {
            combine(
                settingsRepository.current,
                apiConfigurationRepository.isLogged,
            ) { settings, isLogged ->
                settings to isLogged
            }.onEach { (settings, isLogged) ->
                updateState {
                    it.copy(
                        availableTimelineTypes =
                            buildList {
                                this += TimelineType.All
                                if (isLogged) {
                                    this += TimelineType.Subscriptions
                                }
                                this += TimelineType.Local
                            },
                        timelineType =
                            settings?.defaultTimelineType?.toTimelineType()
                                ?: TimelineType.Local,
                        blurNsfw = settings?.blurNsfw ?: true,
                        isLogged = isLogged,
                    )
                }
                refresh(initial = true)
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
                        it.copy(
                            timelineType = intent.type,
                            entries = emptyList(),
                            canFetchMore = false,
                        )
                    }
                    emitEffect(TimelineMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }

            is TimelineMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is TimelineMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is TimelineMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            TimelinePaginationSpecification.Feed(
                timelineType = uiState.value.timelineType,
                includeNsfw = settingsRepository.current.value?.includeNsfw ?: false,
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

    private suspend fun updateEntryInState(
        entryId: String,
        block: (TimelineEntryModel) -> TimelineEntryModel,
    ) {
        updateState {
            it.copy(
                entries =
                    it.entries.map { entry ->
                        when {
                            entry.id == entryId -> {
                                entry.let(block)
                            }

                            entry.reblog?.id == entryId -> {
                                entry.copy(reblog = entry.reblog?.let(block))
                            }

                            else -> {
                                entry
                            }
                        }
                    },
            )
        }
    }

    private fun toggleReblog(entry: TimelineEntryModel) {
        screenModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    reblogLoading = true,
                )
            }
            val newEntry =
                if (entry.reblogged) {
                    timelineEntryRepository.unreblog(entry.id)
                } else {
                    timelineEntryRepository.reblog(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it.copy(
                        reblogged = newEntry.reblogged,
                        reblogCount = newEntry.reblogCount,
                        reblogLoading = false,
                    )
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        reblogLoading = false,
                    )
                }
            }
        }
    }

    private fun toggleFavorite(entry: TimelineEntryModel) {
        screenModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    favoriteLoading = true,
                )
            }
            val newEntry =
                if (entry.favorite) {
                    timelineEntryRepository.unfavorite(entry.id)
                } else {
                    timelineEntryRepository.favorite(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it.copy(
                        favorite = newEntry.favorite,
                        favoriteCount = newEntry.favoriteCount,
                        favoriteLoading = false,
                    )
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        favoriteLoading = false,
                    )
                }
            }
        }
    }

    private fun toggleBookmark(entry: TimelineEntryModel) {
        screenModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    bookmarkLoading = true,
                )
            }
            val newEntry =
                if (entry.bookmarked) {
                    timelineEntryRepository.unbookmark(entry.id)
                } else {
                    timelineEntryRepository.bookmark(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it.copy(
                        bookmarked = newEntry.bookmarked,
                        bookmarkLoading = false,
                    )
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        bookmarkLoading = false,
                    )
                }
            }
        }
    }
}
