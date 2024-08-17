package com.livefast.eattrash.raccoonforfriendica.feature.favorites

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FavoritesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FavoritesPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val type: FavoritesType,
    private val paginationManager: FavoritesPaginationManager,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val settingsRepository: SettingsRepository,
) : DefaultMviModel<FavoritesMviModel.Intent, FavoritesMviModel.State, FavoritesMviModel.Effect>(
        initialState = FavoritesMviModel.State(),
    ),
    FavoritesMviModel {
    init {
        screenModelScope.launch {
            settingsRepository.current
                .onEach { settings ->
                    updateState { it.copy(blurNsfw = settings?.blurNsfw ?: true) }
                }.launchIn(this)
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: FavoritesMviModel.Intent) {
        when (intent) {
            FavoritesMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            FavoritesMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            is FavoritesMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is FavoritesMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is FavoritesMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            when (type) {
                FavoritesType.Bookmarks ->
                    FavoritesPaginationSpecification.Bookmarks(
                        includeNsfw = settingsRepository.current.value?.includeNsfw ?: false,
                    )
                FavoritesType.Favorites ->
                    FavoritesPaginationSpecification.Favorites(
                        includeNsfw = settingsRepository.current.value?.includeNsfw ?: false,
                    )
            },
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

    private suspend fun removeEntryFromState(entryId: String) {
        updateState {
            it.copy(
                entries = it.entries.filter { e -> e.id != entryId && e.reblog?.id != entryId },
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
                if (!newEntry.favorite && type == FavoritesType.Favorites) {
                    removeEntryFromState(entry.id)
                } else {
                    updateEntryInState(entry.id) {
                        it.copy(
                            favorite = newEntry.favorite,
                            favoriteCount = newEntry.favoriteCount,
                            favoriteLoading = false,
                        )
                    }
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
                if (!newEntry.bookmarked && type == FavoritesType.Bookmarks) {
                    removeEntryFromState(entry.id)
                } else {
                    updateEntryInState(entry.id) {
                        it.copy(
                            bookmarked = newEntry.bookmarked,
                            bookmarkLoading = false,
                        )
                    }
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
