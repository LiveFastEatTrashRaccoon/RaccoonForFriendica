package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EventRepository
import dev.mokkery.answering.returns
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

class DefaultEventPaginationManagerTest {
    private val eventRepository = mock<EventRepository>()
    private val sut = DefaultEventPaginationManager(eventRepository = eventRepository)

    @Test
    fun `given no photos when loadNextPage then result is as expected`() = runTest {
        everySuspend { eventRepository.getAll(any()) } returns emptyList()

        sut.reset(EventsPaginationSpecification.All)
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            eventRepository.getAll(pageCursor = null)
        }
    }

    @Test
    fun `given photos when loadNextPage then result is as expected`() = runTest {
        val elements = listOf(EventModel(id = "1", uri = "", title = "", startTime = "0"))
        everySuspend { eventRepository.getAll(any()) } returns elements

        sut.reset(EventsPaginationSpecification.All)
        val res = sut.loadNextPage()

        assertEquals(elements, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            eventRepository.getAll(pageCursor = null)
        }
    }

    @Test
    fun `given can not fetch more when loadNextPage twice then result is as expected`() = runTest {
        val elements = listOf(EventModel(id = "1", uri = "", title = "", startTime = "0"))
        everySuspend {
            eventRepository.getAll(any())
        } sequentiallyReturns
            listOf(
                elements,
                emptyList(),
            )

        sut.reset(EventsPaginationSpecification.All)
        sut.loadNextPage()
        val res = sut.loadNextPage()

        assertEquals(elements, res)
        assertFalse(sut.canFetchMore)
        verifySuspend {
            eventRepository.getAll(pageCursor = null)
            eventRepository.getAll(pageCursor = "1")
        }
    }
}
