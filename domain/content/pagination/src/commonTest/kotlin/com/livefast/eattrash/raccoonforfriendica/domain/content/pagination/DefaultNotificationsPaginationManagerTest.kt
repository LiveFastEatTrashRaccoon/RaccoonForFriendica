package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsArgAt
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultNotificationsPaginationManagerTest {
    private val notificationRepository = mock<NotificationRepository>()
    private val userRepository =
        mock<UserRepository> {
            everySuspend { getRelationships(any()) } returns emptyList()
        }
    private val emojiHelper =
        mock<EmojiHelper> {
            everySuspend { any<UserModel>().withEmojisIfMissing() } returnsArgAt 0
            everySuspend { any<TimelineEntryModel>().withEmojisIfMissing() } returnsArgAt 0
        }
    private val replyHelper =
        mock<ReplyHelper> {
            everySuspend { any<TimelineEntryModel>().withInReplyToIfMissing() } returnsArgAt 0
        }
    private val sut =
        DefaultNotificationsPaginationManager(
            notificationRepository = notificationRepository,
            userRepository = userRepository,
            emojiHelper = emojiHelper,
            replyHelper = replyHelper,
        )

    @Test
    fun `given no results when loadNextPage then result is as expected`() = runTest {
        everySuspend {
            notificationRepository.getAll(
                types = any(),
                pageCursor = any(),
                refresh = any(),
            )
        } returns emptyList()

        sut.reset(
            NotificationsPaginationSpecification.Default(
                types = NotificationType.ALL,
                includeNsfw = false,
            ),
        )
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            notificationRepository.getAll(
                types = NotificationType.ALL,
                pageCursor = null,
                refresh = false,
            )
        }
    }

    @Test
    fun `given results when loadNextPage then result is as expected`() = runTest {
        val elements = listOf(NotificationModel(id = "1"))
        everySuspend {
            notificationRepository.getAll(
                types = any(),
                pageCursor = any(),
                refresh = any(),
            )
        } returns elements

        sut.reset(
            NotificationsPaginationSpecification.Default(
                types = NotificationType.ALL,
                includeNsfw = false,
            ),
        )
        val res = sut.loadNextPage()

        assertEquals(elements, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            notificationRepository.getAll(
                types = NotificationType.ALL,
                pageCursor = null,
                refresh = false,
            )
        }
    }

    @Test
    fun `given results when loadNextPage with refresh then result is as expected`() = runTest {
        val elements = listOf(NotificationModel(id = "1"))
        everySuspend {
            notificationRepository.getAll(
                types = any(),
                pageCursor = any(),
                refresh = any(),
            )
        } returns elements

        sut.reset(
            NotificationsPaginationSpecification.Default(
                types = NotificationType.ALL,
                includeNsfw = false,
                refresh = true,
            ),
        )
        val res = sut.loadNextPage()

        assertEquals(elements, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            notificationRepository.getAll(
                types = NotificationType.ALL,
                pageCursor = null,
                refresh = true,
            )
        }
    }

    @Test
    fun `given sensitive results when loadNextPage without includeNsfw then result is as expected`() = runTest {
        val elements =
            listOf(
                NotificationModel(id = "1"),
                NotificationModel(
                    id = "2",
                    entry = TimelineEntryModel(id = "3", content = "", sensitive = true),
                ),
            )
        everySuspend {
            notificationRepository.getAll(
                types = any(),
                pageCursor = any(),
                refresh = any(),
            )
        } returns elements

        sut.reset(
            NotificationsPaginationSpecification.Default(
                types = NotificationType.ALL,
                includeNsfw = false,
                refresh = true,
            ),
        )
        val res = sut.loadNextPage()

        assertEquals(elements.subList(0, 1), res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            notificationRepository.getAll(
                types = NotificationType.ALL,
                pageCursor = null,
                refresh = true,
            )
        }
    }

    @Test
    fun `given sensitive results when loadNextPage then result is as expected`() = runTest {
        val elements =
            listOf(
                NotificationModel(id = "1"),
                NotificationModel(
                    id = "2",
                    entry = TimelineEntryModel(id = "3", content = "", sensitive = true),
                ),
            )
        everySuspend {
            notificationRepository.getAll(
                types = any(),
                pageCursor = any(),
                refresh = any(),
            )
        } returns elements

        sut.reset(
            NotificationsPaginationSpecification.Default(
                types = NotificationType.ALL,
                includeNsfw = true,
                refresh = true,
            ),
        )
        val res = sut.loadNextPage()

        assertEquals(elements, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            notificationRepository.getAll(
                types = NotificationType.ALL,
                pageCursor = null,
                refresh = true,
            )
        }
    }

    @Test
    fun `given can not fetch more when loadNextPage twice then result is as expected`() = runTest {
        val elements = listOf(NotificationModel(id = "1"))
        everySuspend {
            notificationRepository.getAll(
                types = any(),
                pageCursor = any(),
                refresh = any(),
            )
        } sequentiallyReturns
            listOf(
                elements,
                emptyList(),
            )

        sut.reset(
            NotificationsPaginationSpecification.Default(
                types = NotificationType.ALL,
                includeNsfw = false,
            ),
        )
        sut.loadNextPage()
        val res = sut.loadNextPage()

        assertEquals(elements, res)
        assertFalse(sut.canFetchMore)
        verifySuspend {
            notificationRepository.getAll(
                types = NotificationType.ALL,
                pageCursor = null,
                refresh = false,
            )
            notificationRepository.getAll(
                types = NotificationType.ALL,
                pageCursor = "1",
                refresh = false,
            )
        }
    }
}
