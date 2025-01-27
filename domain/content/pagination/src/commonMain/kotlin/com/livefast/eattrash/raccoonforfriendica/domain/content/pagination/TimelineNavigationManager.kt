package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import kotlinx.coroutines.flow.StateFlow

interface TimelineNavigationManager {
    val canNavigate: StateFlow<Boolean>
    val currentList: List<TimelineEntryModel>

    fun push(state: TimelinePaginationManagerState)

    fun pop()

    suspend fun getPrevious(postId: String): TimelineEntryModel?

    suspend fun getNext(postId: String): TimelineEntryModel?

    suspend fun loadNextPage()
}
