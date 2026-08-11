package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DirectMessageRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import org.koin.core.annotation.Factory

@Factory
internal class DefaultDirectMessagesPaginationManager(
    private val directMessageRepository: DirectMessageRepository,
    private val emojiHelper: EmojiHelper,
) : BasePaginationManager<DirectMessageModel, DirectMessagesPaginationSpecification>(
    idSelector = { it.id },
),
    DirectMessagesPaginationManager {
    private var page = 1

    override suspend fun reset(specification: DirectMessagesPaginationSpecification) {
        super.reset(specification)
        withPaginationLock {
            page = 1
        }
    }

    override suspend fun loadNextPage(): List<DirectMessageModel> {
        val spec = currentSpecification ?: return emptyList()

        val results =
            when (spec) {
                DirectMessagesPaginationSpecification.All ->
                    directMessageRepository
                        .getAll(
                            page = page,
                            limit = 40,
                        )

                is DirectMessagesPaginationSpecification.Replies ->
                    directMessageRepository
                        .getReplies(
                            parentUri = spec.parentUri,
                            page = page,
                        )
            }
        return updateHistory(
            items = results,
            transform = { newItems ->
                if (newItems.isNotEmpty()) {
                    page++
                }
                newItems.fixupCreatorEmojis()
            },
        )
    }

    private suspend fun List<DirectMessageModel>.fixupCreatorEmojis(): List<DirectMessageModel> = with(emojiHelper) {
        map {
            it.copy(
                recipient = it.recipient?.withEmojisIfMissing(),
                sender = it.sender?.withEmojisIfMissing(),
            )
        }
    }
}
