package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

internal class DefaultContentPreloadManager(
    private val timelineEntryRepository: TimelineEntryRepository,
    private val trendingRepository: TrendingRepository,
    private val notificationRepository: NotificationRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ContentPreloadManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override suspend fun preload(userRemoteId: String?) {
        // actually, it is not so important to wait for completion
        // timelineEntryRepository.getByUser may take a long time
        scope.launch {
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
                                notificationRepository.getAll(refresh = true)
                            }
                    }
                }

            tasks.awaitAll().let {}
        }
    }
}
