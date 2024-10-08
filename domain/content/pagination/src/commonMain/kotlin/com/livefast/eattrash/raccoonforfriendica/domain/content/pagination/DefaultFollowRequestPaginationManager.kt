package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository

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
                ?.let { (list, cursor) ->
                    list.deduplicate().updatePaginationData(cursor)
                }?.fixupCreatorEmojis()
                .orEmpty()

        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<UserModel>.updatePaginationData(cursor: String?): List<UserModel> =
        apply {
            pageCursor = cursor
            canFetchMore = isNotEmpty()
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
