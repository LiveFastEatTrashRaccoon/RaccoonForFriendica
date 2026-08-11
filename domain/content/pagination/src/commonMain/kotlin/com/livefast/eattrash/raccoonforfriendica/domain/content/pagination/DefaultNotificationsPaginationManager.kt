package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import org.koin.core.annotation.Factory

@Factory
internal class DefaultNotificationsPaginationManager(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
) : BasePaginationManager<NotificationModel, NotificationsPaginationSpecification>(
    idSelector = { it.id },
),
    NotificationsPaginationManager {

    override suspend fun loadNextPage(): List<NotificationModel> {
        val spec = currentSpecification ?: return emptyList()

        val results =
            when (spec) {
                is NotificationsPaginationSpecification.Default ->
                    notificationRepository
                        .getAll(
                            pageCursor = currentPageCursor,
                            types = spec.types,
                            refresh = spec.refresh,
                        )
            }

        return updateHistory(
            items = results,
            transform = { newItems ->
                newItems
                    .determineRelationshipStatus()
                    .filterNsfw(spec.includeNsfw)
                    .fixupCreatorEmojis()
                    .fixupInReplyTo()
            },
        )
    }

    private suspend fun List<NotificationModel>.determineRelationshipStatus(): List<NotificationModel> = run {
        val userIds = mapNotNull { notification -> notification.user?.id }
        val relationships = userRepository.getRelationships(userIds)
        map { notification ->
            val relationship =
                relationships?.firstOrNull { rel -> rel.id == notification.user?.id }
            notification.copy(
                user =
                notification.user?.copy(
                    relationshipStatus = relationship?.toStatus(),
                    notificationStatus = relationship?.toNotificationStatus(),
                ),
            )
        }
    }

    private fun List<NotificationModel>.filterNsfw(included: Boolean): List<NotificationModel> =
        filter { included || it.entry?.isNsfw != true }

    private suspend fun List<NotificationModel>.fixupCreatorEmojis(): List<NotificationModel> = with(emojiHelper) {
        map {
            it.copy(
                user = it.user?.withEmojisIfMissing(),
                entry = it.entry?.withEmojisIfMissing(),
            )
        }
    }

    private suspend fun List<NotificationModel>.fixupInReplyTo(): List<NotificationModel> = with(replyHelper) {
        map {
            it.copy(
                entry = it.entry?.withInReplyToIfMissing(),
            )
        }
    }
}
