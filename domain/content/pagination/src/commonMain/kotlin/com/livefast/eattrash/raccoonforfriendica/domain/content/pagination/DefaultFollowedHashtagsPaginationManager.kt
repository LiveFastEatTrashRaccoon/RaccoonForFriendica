package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TagRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(scope = AppScope::class, binding = binding<FollowedHashtagsPaginationManager>())
@Inject
class DefaultFollowedHashtagsPaginationManager(private val tagRepository: TagRepository) :
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
