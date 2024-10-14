package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendingRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal class DefaultContentPreloadManager(
    private val timelineEntryRepository: TimelineEntryRepository,
    private val trendingRepository: TrendingRepository,
    private val notificationRepository: NotificationRepository,
) : ContentPreloadManager {
    override suspend fun preload(userRemoteId: String?) =
        coroutineScope {
            val tasks =
                buildList {
                    this +=
                        async {
                            trendingRepository.getHashtags(
                                offset = 0,
                                refresh = true,
                            )
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
                            }

                        this +=
                            async {
                                notificationRepository.getAll(
                                    includeAll = true,
                                    refresh = true,
                                )
                            }
                    }
                }

            tasks.awaitAll().let {}
        }
}
