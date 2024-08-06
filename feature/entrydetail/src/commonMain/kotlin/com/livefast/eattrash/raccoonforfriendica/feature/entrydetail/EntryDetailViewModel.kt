package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import kotlinx.coroutines.launch

class EntryDetailViewModel(
    private val id: String,
    private val timelineRepository: TimelineRepository,
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
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val entry = timelineRepository.getById(id)
        val context = timelineRepository.getContext(id)
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
}
