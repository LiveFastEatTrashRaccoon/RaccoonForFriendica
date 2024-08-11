package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import kotlinx.coroutines.launch

class EntryDetailViewModel(
    private val id: String,
    private val timelineEntryRepository: TimelineEntryRepository,
) : DefaultMviModel<EntryDetailMviModel.Intent, EntryDetailMviModel.State, EntryDetailMviModel.Effect>(
        initialState = EntryDetailMviModel.State(),
    ),
    EntryDetailMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: EntryDetailMviModel.Intent) {
        when (intent) {
            EntryDetailMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            is EntryDetailMviModel.Intent.ToggleReblog -> toggleReblog(intent.entryId)
            is EntryDetailMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entryId)
            is EntryDetailMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entryId)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val entry = timelineEntryRepository.getById(id)
        val context = timelineEntryRepository.getContext(id)
        val entries =
            buildList {
                addAll(context?.ancestors.orEmpty())
                if (entry != null) {
                    add(entry)
                }
                addAll(context?.descendants.orEmpty())
            }

        updateState {
            it.copy(
                creator = entry?.creator,
                entries = entries,
                refreshing = false,
                initial = false,
            )
        }

        if (initial) {
            val index = entries.indexOf(entry)
            emitEffect(EntryDetailMviModel.Effect.ScrollToItem(index))
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
                updateEntryInState(entryId) {
                    it.copy(
                        favorite = newEntry.favorite,
                        favoriteCount = newEntry.favoriteCount,
                    )
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
                updateEntryInState(entryId) {
                    it.copy(
                        bookmarked = newEntry.bookmarked,
                    )
                }
            }
        }
    }
}
