package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
internal class DefaultFollowedHashtagsPaginationManager(private val tagRepository: TagRepository) :
    BasePaginationManager<TagModel, Unit>(
        idSelector = { it.name },
    ),
    FollowedHashtagsPaginationManager {

    override suspend fun reset() {
        super.reset(Unit)
    }

    override suspend fun loadNextPage(): List<TagModel> {
        val results = tagRepository.getFollowed(currentPageCursor)

        return updateHistory(
            results = results,
        )
    }
}
