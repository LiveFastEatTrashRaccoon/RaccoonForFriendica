package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.NotificationCenterEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledPostRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultUnpublishedPaginationManagerTest {
    private val scheduledPostRepository = mock<ScheduledPostRepository>()
    private val draftRepository = mock<DraftRepository>()
    private val notificationCenter =
        mock<NotificationCenter> {
            every { subscribe(any<KClass<NotificationCenterEvent>>()) } returns MutableSharedFlow()
        }
    private val sut =
        DefaultUnpublishedPaginationManager(
            scheduledPostRepository = scheduledPostRepository,
            draftRepository = draftRepository,
            notificationCenter = notificationCenter,
        )

    // region Scheduled
    @Test
    fun `given no results when loadNextPage with Scheduled then result is as expected`() = runTest {
        everySuspend { scheduledPostRepository.getAll(any()) } returns emptyList()

        sut.reset(UnpublishedPaginationSpecification.Scheduled)
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            scheduledPostRepository.getAll(null)
        }
    }

    @Test
    fun `given results when loadNextPage with Scheduled then result is as expected`() = runTest {
        val list = listOf(PostModel(id = "1", content = ""))
        everySuspend { scheduledPostRepository.getAll(any()) } returns list

        sut.reset(UnpublishedPaginationSpecification.Scheduled)
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            scheduledPostRepository.getAll(null)
        }
    }

    @Test
    fun `given can not fetch more results when loadNextPage with Scheduled twice then result is as expected`() =
        runTest {
            val list = listOf(PostModel(id = "1", content = ""))
            everySuspend { scheduledPostRepository.getAll(any()) } sequentiallyReturns
                listOf(list, emptyList())

            sut.reset(UnpublishedPaginationSpecification.Scheduled)
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                scheduledPostRepository.getAll(null)
                scheduledPostRepository.getAll("1")
            }
        }
    // endregion

    // region Drafts
    @Test
    fun `given no results when loadNextPage with Drafts then result is as expected`() = runTest {
        everySuspend { draftRepository.getAll(any()) } returns emptyList()

        sut.reset(UnpublishedPaginationSpecification.Drafts)
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            draftRepository.getAll(0)
        }
    }

    @Test
    fun `given results when loadNextPage with Drafts then result is as expected`() = runTest {
        val list = listOf(PostModel(id = "1", content = ""))
        everySuspend { draftRepository.getAll(any()) } returns list

        sut.reset(UnpublishedPaginationSpecification.Drafts)
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            draftRepository.getAll(0)
        }
    }

    @Test
    fun `given can not fetch more results when loadNextPage with Drafts twice then result is as expected`() = runTest {
        val list = listOf(PostModel(id = "1", content = ""))
        everySuspend { draftRepository.getAll(any()) } sequentiallyReturns
            listOf(list, emptyList())

        sut.reset(UnpublishedPaginationSpecification.Drafts)
        sut.loadNextPage()
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertFalse(sut.canFetchMore)
        verifySuspend {
            draftRepository.getAll(0)
            draftRepository.getAll(1)
        }
    }
    // endregion
}
