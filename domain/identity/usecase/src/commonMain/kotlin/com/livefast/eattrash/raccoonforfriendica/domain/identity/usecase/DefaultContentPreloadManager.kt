package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendingRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal class DefaultContentPreloadManager(
    private val timelineRepository: TimelineRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val trendingRepository: TrendingRepository,
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
) : ContentPreloadManager {
    override suspend fun preload(
        userRemoteId: String?,
        defaultTimelineType: TimelineType,
    ) = coroutineScope {
        val tasks =
            buildList {
                this +=
                    async {
                        when (defaultTimelineType) {
                            TimelineType.All ->
                                timelineRepository.getPublic(refresh = true)

                            TimelineType.Local ->
                                timelineRepository.getLocal(refresh = true)

                            TimelineType.Subscriptions ->
                                timelineRepository.getHome(refresh = true)

                            else -> Unit
                        }
                    }
                this +=
                    async {
                        trendingRepository.getHashtags(
                            offset = 0,
                            refresh = true,
                        )
                    }

                this +=
                    async {
                        userRepository.getCurrent(refresh = true)
                    }

                if (userRemoteId != null) {
                    this +=
                        async {
                            timelineEntryRepository.getByUser(
                                userId = userRemoteId,
                                refresh = true,
                                enableCache = true,
                                excludeReplies = true,
                            )
                            notificationRepository.getAll(
                                includeAll = true,
                                refresh = true,
                            )
                        }
                }
            }

        tasks.awaitAll()

        Unit
    }
}
