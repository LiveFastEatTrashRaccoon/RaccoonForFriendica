package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import kotlinx.coroutines.flow.MutableStateFlow

internal class DefaultTimelineNavigationManager(
    private val paginationManager: TimelinePaginationManager,
) : TimelineNavigationManager {
    override val canNavigate = MutableStateFlow(false)
    private var states: MutableList<TimelinePaginationManagerState> = mutableListOf()

    override val currentList: List<TimelineEntryModel>
        get() = paginationManager.history

    override fun push(state: TimelinePaginationManagerState) {
        states += state
        canNavigate.value = true
        paginationManager.restoreState(state)
    }

    override fun pop() {
        states.removeLastOrNull()
        val canStillNavigate = states.isNotEmpty()
        canNavigate.value = canStillNavigate
        if (canStillNavigate) {
            states.lastOrNull()?.also { lastState ->
                paginationManager.restoreState(lastState)
            }
        }
    }

    override suspend fun getPrevious(postId: String): TimelineEntryModel? {
        val history = paginationManager.history
        val index =
            history
                .indexOfFirst { it.id == postId }
                .takeIf { it >= 0 } ?: return null
        return when (index) {
            0 -> null
            else -> history.getOrNull(index - 1)
        }
    }

    override suspend fun getNext(postId: String): TimelineEntryModel? {
        val history = paginationManager.history
        val index =
            history
                .indexOfFirst { it.id == postId }
                .takeIf { it >= 0 } ?: return null
        return when {
            index < history.lastIndex -> history[index + 1]
            !paginationManager.canFetchMore -> null
            else -> {
                val newPosts = paginationManager.loadNextPage()
                val newIndex = newPosts.indexOfFirst { it.id == postId }
                newPosts.getOrNull(newIndex + 1)
            }
        }
    }

    override suspend fun loadNextPage() {
        paginationManager.loadNextPage()
    }
}
