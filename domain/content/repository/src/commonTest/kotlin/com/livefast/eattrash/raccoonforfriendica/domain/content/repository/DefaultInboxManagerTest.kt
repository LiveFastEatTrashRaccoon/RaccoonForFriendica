package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.PagedList
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultInboxManagerTest {
    private val notificationRepository = mock<NotificationRepository>()
    private val markerRepository =
        mock<MarkerRepository> {
            everySuspend { get(type = any(), refresh = any()) } returns
                MarkerModel(type = MarkerType.Notifications, lastReadId = "0")
        }
    private val sut =
        DefaultInboxManager(
            notificationRepository = notificationRepository,
            markerRepository = markerRepository,
        )

    @Test
    fun `given no results when refreshUnreadCount then count is as expected`() = runTest {
        everySuspend {
            notificationRepository.getAll(
                types = any(),
                pageCursor = any(),
                refresh = any(),
            )
        } returns PagedList(emptyList())

        sut.refreshUnreadCount()
        val res = sut.unreadCount.value

        assertEquals(0, res)
        verifySuspend {
            markerRepository.get(MarkerType.Notifications)
            notificationRepository.getAll(
                refresh = true,
                pageCursor = null,
                types = NotificationType.ALL,
            )
        }
    }

    @Test
    fun `given results when refreshUnreadCount then count is as expected`() = runTest {
        val list =
            listOf(
                NotificationModel(id = "1", post = null),
                NotificationModel(id = "2", post = null),
            )
        everySuspend {
            notificationRepository.getAll(
                types = any(),
                pageCursor = any(),
                refresh = any(),
            )
        } returns PagedList(list)

        sut.refreshUnreadCount()
        val res = sut.unreadCount.value

        assertEquals(2, res)
        verifySuspend {
            markerRepository.get(MarkerType.Notifications)
            notificationRepository.getAll(
                refresh = true,
                pageCursor = null,
                types = NotificationType.ALL,
            )
        }
    }

    @Test
    fun `when clearUnreadCount then count is as expected`() = runTest {
        val list =
            listOf(
                NotificationModel(id = "1", post = null),
                NotificationModel(id = "2", post = null),
            )
        everySuspend {
            notificationRepository.getAll(
                types = any(),
                pageCursor = any(),
                refresh = any(),
            )
        } returns PagedList(list)
        sut.refreshUnreadCount()
        val resBefore = sut.unreadCount.value
        assertEquals(2, resBefore)

        sut.clearUnreadCount()
        val res = sut.unreadCount.value
        assertEquals(0, res)

        verifySuspend {
            markerRepository.get(MarkerType.Notifications)
            notificationRepository.getAll(
                refresh = true,
                pageCursor = null,
                types = NotificationType.ALL,
            )
        }
    }

    @Test
    fun `when decrementUnreadCount then count is as expected`() = runTest {
        val list =
            listOf(
                NotificationModel(id = "1", post = null),
                NotificationModel(id = "2", post = null),
            )
        everySuspend {
            notificationRepository.getAll(
                types = any(),
                pageCursor = any(),
                refresh = any(),
            )
        } returns PagedList(list)
        sut.refreshUnreadCount()
        val resBefore = sut.unreadCount.value
        assertEquals(2, resBefore)

        sut.decrementUnreadCount()
        val res = sut.unreadCount.value
        assertEquals(1, res)
        verifySuspend {
            markerRepository.get(MarkerType.Notifications)
            notificationRepository.getAll(
                refresh = true,
                pageCursor = null,
                types = NotificationType.ALL,
            )
        }
    }
}
