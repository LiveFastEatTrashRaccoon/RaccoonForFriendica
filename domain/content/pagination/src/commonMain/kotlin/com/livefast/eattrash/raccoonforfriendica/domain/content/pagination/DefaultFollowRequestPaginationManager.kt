package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import org.koin.core.annotation.Factory

@Factory
internal class DefaultFollowRequestPaginationManager(
    private val userRepository: UserRepository,
    private val emojiHelper: EmojiHelper,
) : BasePaginationManager<UserModel, Unit>(
    idSelector = { it.id },
),
    FollowRequestPaginationManager {

    override suspend fun reset() {
        super.reset(Unit)
    }

    override suspend fun loadNextPage(): List<UserModel> {
        val results = userRepository.getFollowRequests(currentPageCursor)

        return updateHistory(
            results = results,
            transform = { newItems ->
                newItems.fixupCreatorEmojis()
            },
        )
    }

    private suspend fun List<UserModel>.fixupCreatorEmojis(): List<UserModel> = with(emojiHelper) {
        map {
            it.withEmojisIfMissing()
        }
    }
}
