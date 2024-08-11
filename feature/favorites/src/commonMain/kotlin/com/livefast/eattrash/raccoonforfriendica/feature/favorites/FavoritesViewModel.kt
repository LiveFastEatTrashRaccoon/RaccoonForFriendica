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

            is FavoritesMviModel.Intent.ToggleReblog -> toggleReblog(intent.entryId)
            is FavoritesMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entryId)
            is FavoritesMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entryId)
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
                        if (entry.id == entryId) {
                            entry.let(block)
                        } else {
                            entry
                        }
                    },
            )
        }
    }

    private suspend fun removeEntryFromState(entryId: String) {
        updateState {
            it.copy(
                entries = it.entries.filter { e -> e.id != entryId },
            )
        }
    }

    private fun toggleReblog(entryId: String) {
        val entry = uiState.value.entries.firstOrNull { it.id == entryId } ?: return
        screenModelScope.launch {
            val newEntry =
                if (entry.reblogged) {
                    timelineEntryRepository.unreblog(entryId)
                } else {
                    timelineEntryRepository.reblog(entryId)
                }
            if (newEntry != null) {
                updateEntryInState(entryId) {
                    it.copy(
                        reblogged = newEntry.reblogged,
                        reblogCount = newEntry.reblogCount,
                    )
                }
            }
        }
    }

    private fun toggleFavorite(entryId: String) {
        val entry = uiState.value.entries.firstOrNull { it.id == entryId } ?: return
        screenModelScope.launch {
            val newEntry =
                if (entry.favorite) {
                    timelineEntryRepository.unfavorite(entryId)
                } else {
                    timelineEntryRepository.favorite(entryId)
                }
            if (newEntry != null) {
                if (!newEntry.favorite && type == FavoritesType.Favorites) {
                    removeEntryFromState(entryId)
                } else {
                    updateEntryInState(entryId) {
                        it.copy(
                            favorite = newEntry.favorite,
                            favoriteCount = newEntry.favoriteCount,
                        )
                    }
                }
            }
        }
    }

    private fun toggleBookmark(entryId: String) {
        val entry = uiState.value.entries.firstOrNull { it.id == entryId } ?: return
        screenModelScope.launch {
            val newEntry =
                if (entry.bookmarked) {
                    timelineEntryRepository.unbookmark(entryId)
                } else {
                    timelineEntryRepository.bookmark(entryId)
                }
            if (newEntry != null) {
                if (!newEntry.bookmarked && type == FavoritesType.Bookmarks) {
                    removeEntryFromState(entryId)
                } else {
                    updateEntryInState(entryId) {
                        it.copy(
                            bookmarked = newEntry.bookmarked,
                        )
                    }
                }
            }
        }
    }
}
