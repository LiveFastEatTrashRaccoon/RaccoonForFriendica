package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AnnouncementModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultAnnouncementsManagerTest {
    private val announcementRepository = mock<AnnouncementRepository>()
    private val sut =
        DefaultAnnouncementsManager(
            announcementRepository = announcementRepository,
        )

    @Test
    fun `given no results when refreshUnreadCount then count is as expected`() = runTest {
        val list = emptyList<AnnouncementModel>()
        everySuspend { announcementRepository.getAll(any()) } returns list

        sut.refreshUnreadCount()
        val res = sut.unreadCount.value

        assertEquals(0, res)
        verifySuspend {
            announcementRepository.getAll(refresh = true)
        }
    }

    @Test
    fun `given results when refreshUnreadCount then count is as expected`() = runTest {
        val list =
            listOf(
                AnnouncementModel("1", ""),
                AnnouncementModel("2", ""),
            )
        everySuspend { announcementRepository.getAll(any()) } returns list

        sut.refreshUnreadCount()
        val res = sut.unreadCount.value

        assertEquals(2, res)
        verifySuspend {
            announcementRepository.getAll(refresh = true)
        }
    }

    @Test
    fun `when refreshUnreadCount then count is as expected`() = runTest {
        val list =
            listOf(
                AnnouncementModel("1", ""),
                AnnouncementModel("2", ""),
            )
        everySuspend { announcementRepository.getAll(any()) } returns list
        sut.refreshUnreadCount()
        val resBefore = sut.unreadCount.value
        assertEquals(2, resBefore)

        sut.clearUnreadCount()
        val res = sut.unreadCount.value
        assertEquals(0, res)
    }

    @Test
    fun `when decrementUnreadCount then count is as expected`() = runTest {
        val list =
            listOf(
                AnnouncementModel("1", ""),
                AnnouncementModel("2", ""),
            )
        everySuspend { announcementRepository.getAll(any()) } returns list
        sut.refreshUnreadCount()
        val resBefore = sut.unreadCount.value
        assertEquals(2, resBefore)

        sut.decrementUnreadCount()
        val res = sut.unreadCount.value
        assertEquals(1, res)
    }
}
