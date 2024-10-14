package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendingRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultContentPreloadManagerTest {
    private val timelineEntryRepository =
        mock<TimelineEntryRepository> {
            everySuspend {
                getByUser(any(), any(), any(), any(), any(), any(), any(), any())
            } returns listOf()
        }
    private val trendingRepository =
        mock<TrendingRepository> {
            everySuspend { getHashtags(any(), any()) } returns listOf()
        }
    private val notificationRepository =
        mock<NotificationRepository> {
            everySuspend { getAll(any(), any(), any()) } returns listOf()
        }
    private val sut =
        DefaultContentPreloadManager(
            timelineEntryRepository = timelineEntryRepository,
            trendingRepository = trendingRepository,
            notificationRepository = notificationRepository,
        )

    @Test
    fun `given anonymous account when preload then interactions are as expected`() =
        runTest {
            sut.preload()

            verifySuspend {
                trendingRepository.getHashtags(
                    offset = 0,
                    refresh = true,
                )
            }
        }

    @Test
    fun `given remote account when preload then interactions are as expected`() =
        runTest {
            val userId = "fake-user-id"
            sut.preload(userId)

            verifySuspend {
                trendingRepository.getHashtags(
                    offset = 0,
                    refresh = true,
                )
                timelineEntryRepository.getByUser(
                    userId = userId,
                    excludeReplies = true,
                    enableCache = true,
                    refresh = true,
                )
                notificationRepository.getAll(refresh = true)
            }
        }
}
