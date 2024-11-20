package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
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
            everySuspend {
                get(any(), any())
            } returns MarkerModel(type = MarkerType.Notifications, lastReadId = "0")
        }
    private val sut =
        DefaultInboxManager(
            notificationRepository = notificationRepository,
            markerRepository = markerRepository,
        )

    @Test
    fun `given no results when refreshUnreadCount then count is as expected`() =
        runTest {
            val list = emptyList<NotificationModel>()
            everySuspend { notificationRepository.getAll(any(), any(), any()) } returns list

            sut.refreshUnreadCount()
            val res = sut.unreadCount.value

            assertEquals(0, res)
            verifySuspend {
                notificationRepository.getAll(
                    refresh = true,
                    pageCursor = null,
                    types = NotificationType.ALL,
                )
            }
        }

    @Test
    fun `given results when refreshUnreadCount then count is as expected`() =
        runTest {
            val list =
                listOf(
                    NotificationModel(id = "1", entry = null),
                    NotificationModel(id = "2", entry = null),
                )
            everySuspend { notificationRepository.getAll(any(), any(), any()) } returns list

            sut.refreshUnreadCount()
            val res = sut.unreadCount.value

            assertEquals(2, res)
            verifySuspend {
                notificationRepository.getAll(
                    refresh = true,
                    pageCursor = null,
                    types = NotificationType.ALL,
                )
            }
        }

    @Test
    fun `when refreshUnreadCount then count is as expected`() =
        runTest {
            val list =
                listOf(
                    NotificationModel(id = "1", entry = null),
                    NotificationModel(id = "2", entry = null),
                )
            everySuspend { notificationRepository.getAll(any(), any(), any()) } returns list
            sut.refreshUnreadCount()
            val resBefore = sut.unreadCount.value
            assertEquals(2, resBefore)

            sut.clearUnreadCount()
            val res = sut.unreadCount.value
            assertEquals(0, res)
        }

    @Test
    fun `when decrementUnreadCount then count is as expected`() =
        runTest {
            val list =
                listOf(
                    NotificationModel(id = "1", entry = null),
                    NotificationModel(id = "2", entry = null),
                )
            everySuspend { notificationRepository.getAll(any(), any(), any()) } returns list
            sut.refreshUnreadCount()
            val resBefore = sut.unreadCount.value
            assertEquals(2, resBefore)

            sut.decrementUnreadCount()
            val res = sut.unreadCount.value
            assertEquals(1, res)
        }
}
