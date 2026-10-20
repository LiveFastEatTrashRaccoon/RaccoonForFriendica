package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import kotlinx.coroutines.flow.StateFlow

interface TimelineNavigationManager {
    val canNavigate: StateFlow<Boolean>
    val currentList: List<PostModel>

    fun push(state: TimelinePaginationManagerState)

    fun pop()

    suspend fun getPrevious(postId: String): PostModel?

    suspend fun getNext(postId: String): PostModel?

    suspend fun loadNextPage()
}
