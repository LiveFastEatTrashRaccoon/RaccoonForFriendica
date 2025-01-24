package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultTimelineNavigationManagerTest {
    private val paginationManager = mock<TimelinePaginationManager>(MockMode.autoUnit)
    private val sut =
        DefaultTimelineNavigationManager(paginationManager = paginationManager)

    @Test
    fun whenInitial_thenCanNotNavigate() =
        runTest {
            val res = sut.canNavigate.value
            assertFalse(res)
        }

    @Test
    fun whenPush_thenCanNavigate() =
        runTest {
            val mockState = mock<TimelinePaginationManagerState>()
            sut.push(mockState)

            val res = sut.canNavigate.value
            assertTrue(res)

            verify {
                paginationManager.restoreState(mockState)
            }
        }

    @Test
    fun givenEmpty_whenPop_thenCanNotNavigate() {
        sut.pop()

        val res = sut.canNavigate.value

        assertFalse(res)
    }

    @Test
    fun whenPopWithMoreThanOneState_thenCanNavigate() =
        runTest {
            val mockState1 = mock<TimelinePaginationManagerState>()
            sut.push(mockState1)
            val mockState2 = mock<TimelinePaginationManagerState>()
            sut.push(mockState2)

            sut.pop()
            val res = sut.canNavigate.value
            assertTrue(res)

            verify(VerifyMode.order) {
                paginationManager.restoreState(mockState1)
                paginationManager.restoreState(mockState2)
                paginationManager.restoreState(mockState1)
            }
        }

    @Test
    fun whenPopWithOneState_thenCanNotNavigate() =
        runTest {
            val mockState = mock<TimelinePaginationManagerState>()
            sut.push(mockState)

            sut.pop()
            val res = sut.canNavigate.value
            assertFalse(res)

            verify(VerifyMode.order) {
                paginationManager.restoreState(mockState)
            }
        }

    @Test
    fun givenEmptyHistory_whenGetPrevious_thenResultIsAsExpected() =
        runTest {
            every { paginationManager.history } returns emptyList()

            val res = sut.getPrevious("1")

            assertNull(res)
        }

    @Test
    fun givenHistory_whenGetPreviousWithFirstId_thenResultIsAsExpected() =
        runTest {
            val postId = "1"
            every { paginationManager.history } returns
                listOf(TimelineEntryModel(id = postId, content = ""))

            val res = sut.getPrevious(postId)

            assertNull(res)
        }

    @Test
    fun givenHistory_whenGetPreviousWithNotFirstId_thenResultIsAsExpected() =
        runTest {
            val otherPostId = "1"
            val postId = "2"
            every { paginationManager.history } returns
                listOf(
                    TimelineEntryModel(id = otherPostId, content = ""),
                    TimelineEntryModel(id = postId, content = ""),
                )

            val res = sut.getPrevious(postId)

            assertNotNull(res)
            assertEquals(otherPostId, res.id)
        }

    @Test
    fun givenEmptyHistory_whenGetNext_thenResultIsAsExpected() =
        runTest {
            every { paginationManager.history } returns emptyList()

            val res = sut.getNext("1")

            assertNull(res)
        }

    @Test
    fun givenHistoryAndCanNotFetchMore_whenGetNextWithLastId_thenResultIsAsExpected() =
        runTest {
            val postId = "1"
            every { paginationManager.history } returns
                listOf(TimelineEntryModel(id = postId, content = ""))
            every { paginationManager.canFetchMore } returns false

            val res = sut.getNext(postId)

            assertNull(res)
            verifySuspend(VerifyMode.not) {
                paginationManager.loadNextPage()
            }
        }

    @Test
    fun givenHistoryAndCanFetchMore_whenGetNextWithLastId_thenResultIsAsExpected() =
        runTest {
            val postId = "1"
            val otherPostId = "2"
            every { paginationManager.history } returns
                listOf(TimelineEntryModel(id = postId, content = ""))
            every { paginationManager.canFetchMore } returns true
            everySuspend { paginationManager.loadNextPage() } returns
                listOf(TimelineEntryModel(id = otherPostId, content = ""))

            val res = sut.getNext(postId)

            assertNotNull(res)
            assertEquals(otherPostId, res.id)

            verifySuspend {
                paginationManager.loadNextPage()
            }
        }

    @Test
    fun givenHistory_whenGetPreviousWithNotLastId_thenResultIsAsExpected() =
        runTest {
            val otherPostId = "1"
            val postId = "2"
            every { paginationManager.history } returns
                listOf(
                    TimelineEntryModel(id = postId, content = ""),
                    TimelineEntryModel(id = otherPostId, content = ""),
                )

            val res = sut.getNext(postId)

            assertNotNull(res)
            assertEquals(otherPostId, res.id)
        }
}
