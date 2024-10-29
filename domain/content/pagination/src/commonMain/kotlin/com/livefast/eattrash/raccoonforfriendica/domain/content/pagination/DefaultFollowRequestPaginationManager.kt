package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor

internal class DefaultFollowRequestPaginationManager(
    private val userRepository: UserRepository,
    private val emojiHelper: EmojiHelper,
) : FollowRequestPaginationManager {
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<UserModel>()

    override suspend fun reset() {
        pageCursor = null
        history.clear()
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<UserModel> {
        val results =
            userRepository
                .getFollowRequests(pageCursor)
                ?.updatePaginationData()
                ?.deduplicate()
                ?.fixupCreatorEmojis()
                .orEmpty()

        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun ListWithPageCursor<UserModel>.updatePaginationData(): List<UserModel> =
        run {
            pageCursor = cursor
            canFetchMore = list.isNotEmpty()
            list
        }

    private fun List<UserModel>.deduplicate(): List<UserModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.id }

    private suspend fun List<UserModel>.fixupCreatorEmojis(): List<UserModel> =
        with(emojiHelper) {
            map {
                it.withEmojisIfMissing()
            }
        }
}
