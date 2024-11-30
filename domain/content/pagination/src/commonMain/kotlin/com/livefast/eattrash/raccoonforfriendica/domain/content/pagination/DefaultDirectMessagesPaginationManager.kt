package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DirectMessageRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultDirectMessagesPaginationManager(
    private val directMessageRepository: DirectMessageRepository,
    private val emojiHelper: EmojiHelper,
) : DirectMessagesPaginationManager {
    private var specification: DirectMessagesPaginationSpecification? = null
    private var page = 1
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<DirectMessageModel>()
    private val mutex = Mutex()

    override suspend fun reset(specification: DirectMessagesPaginationSpecification) {
        this.specification = specification
        page = 1
        mutex.withLock {
            history.clear()
        }
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<DirectMessageModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                DirectMessagesPaginationSpecification.All ->
                    directMessageRepository
                        .getAll(
                            page = page,
                            limit = 40,
                        ).orEmpty()

                is DirectMessagesPaginationSpecification.Replies ->
                    directMessageRepository
                        .getReplies(
                            parentUri = specification.parentUri,
                            page = page,
                        ).orEmpty()
            }
        return mutex.withLock {
            results
                .deduplicate()
                .updatePaginationData()
                .fixupCreatorEmojis()
                .also { history.addAll(it) }
            // return a copy
            history.map { it }
        }
    }

    private fun List<DirectMessageModel>.updatePaginationData(): List<DirectMessageModel> =
        apply {
            val hasData = isNotEmpty()
            if (hasData) {
                page++
            }
            canFetchMore = hasData
        }

    private fun List<DirectMessageModel>.deduplicate(): List<DirectMessageModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.id }

    private suspend fun List<DirectMessageModel>.fixupCreatorEmojis(): List<DirectMessageModel> =
        with(emojiHelper) {
            map {
                it.copy(
                    recipient = it.recipient?.withEmojisIfMissing(),
                    sender = it.sender?.withEmojisIfMissing(),
                )
            }
        }
}
