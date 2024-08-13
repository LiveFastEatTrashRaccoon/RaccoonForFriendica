package com.livefast.eattrash.raccoonforfriendica.feature.favorites

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FavoritesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FavoritesPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val type: FavoritesType,
    private val paginationManager: FavoritesPaginationManager,
    private val timelineEntryRepository: TimelineEntryRepository,
) : DefaultMviModel<FavoritesMviModel.Intent, FavoritesMviModel.State, FavoritesMviModel.Effect>(
        initialState = FavoritesMviModel.State(),
    ),
    FavoritesMviModel {
    init {
        screenModelScope.launch {
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
                FavoritesType.Bookmarks -> FavoritesPaginationSpecification.Bookmarks
                FavoritesType.Favorites -> FavoritesPaginationSpecification.Favorites
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
                    )
                }
            }
        }
    }

    private fun toggleFavorite(entry: TimelineEntryModel) {
        screenModelScope.launch {
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
                        )
                    }
                }
            }
        }
    }

    private fun toggleBookmark(entry: TimelineEntryModel) {
        screenModelScope.launch {
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
                        )
                    }
                }
            }
        }
    }
}
