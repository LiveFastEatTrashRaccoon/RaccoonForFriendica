package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultToggleEntryDislikeUseCaseTest {
    private val entryRepository = mock<TimelineEntryRepository>()
    private val sut = DefaultToggleEntryDislikeUseCase(entryRepository)

    @Test
    fun `given entry not disliked and not favorite when invoked then result and interactions are as expected`() =
        runTest {
            everySuspend { entryRepository.dislike(any()) } returns true
            val entryId = "1"
            val entry = TimelineEntryModel(id = entryId, content = "")

            val res = sut(entry)

            assertNotNull(res)
            assertEquals(1, res.dislikesCount)
            assertTrue(res.disliked)
            assertFalse(res.favorite)
            verifySuspend {
                entryRepository.dislike(entryId)
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.unfavorite(any())
            }
        }

    @Test
    fun `given entry not disliked and not favorite but dislike error when invoked then result and interactions are as expected`() =
        runTest {
            everySuspend { entryRepository.dislike(any()) } returns false
            val entryId = "1"
            val entry = TimelineEntryModel(id = entryId, content = "")

            val res = sut(entry)

            assertNull(res)
            verifySuspend {
                entryRepository.dislike(entryId)
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.unfavorite(any())
            }
        }

    @Test
    fun `given entry not disliked and favorite when invoked then result and interactions are as expected`() =
        runTest {
            everySuspend { entryRepository.dislike(any()) } returns true
            everySuspend { entryRepository.unfavorite(any()) } calls { args ->
                val entryId: String = args.arg(0)
                TimelineEntryModel(
                    id = entryId,
                    content = "",
                    favoriteCount = 0,
                    favorite = false,
                )
            }
            val entryId = "1"
            val entry =
                TimelineEntryModel(id = entryId, content = "", favorite = true, favoriteCount = 1)

            val res = sut(entry)

            assertNotNull(res)
            assertEquals(1, res.dislikesCount)
            assertTrue(res.disliked)
            assertFalse(res.favorite)
            assertEquals(0, res.favoriteCount)
            verifySuspend {
                entryRepository.dislike(entryId)
                entryRepository.unfavorite(entryId)
            }
        }

    @Test
    fun `given entry not disliked and favorite but favorite error when invoked then result and interactions are as expected`() =
        runTest {
            everySuspend { entryRepository.dislike(any()) } returns true
            everySuspend { entryRepository.unfavorite(any()) } returns null
            val entryId = "1"
            val entry =
                TimelineEntryModel(id = entryId, content = "", favorite = true, favoriteCount = 1)

            val res = sut(entry)

            assertNull(res)
            verifySuspend {
                entryRepository.unfavorite(entryId)
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.dislike(entryId)
            }
        }

    @Test
    fun `given entry not disliked and favorite but dislike error when invoked then result and interactions are as expected`() =
        runTest {
            everySuspend { entryRepository.dislike(any()) } returns false
            everySuspend { entryRepository.unfavorite(any()) } calls { args ->
                val entryId: String = args.arg(0)
                TimelineEntryModel(
                    id = entryId,
                    content = "",
                    favoriteCount = 0,
                    favorite = false,
                )
            }
            val entryId = "1"
            val entry =
                TimelineEntryModel(id = entryId, content = "", favorite = true, favoriteCount = 1)

            val res = sut(entry)

            assertNull(res)
            verifySuspend {
                entryRepository.unfavorite(entryId)
                entryRepository.dislike(entryId)
            }
        }

    @Test
    fun `given entry disliked and not favorite when invoked then result and interactions are as expected`() =
        runTest {
            everySuspend { entryRepository.undislike(any()) } returns true
            val entryId = "1"
            val entry =
                TimelineEntryModel(id = entryId, content = "", disliked = true, dislikesCount = 1)

            val res = sut(entry)

            assertNotNull(res)
            assertEquals(0, res.dislikesCount)
            assertFalse(res.disliked)
            assertFalse(res.favorite)
            verifySuspend {
                entryRepository.undislike(entryId)
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.unfavorite(any())
            }
        }

    @Test
    fun `given entry disliked and not favorite but dislike error when invoked then result and interactions are as expected`() =
        runTest {
            everySuspend { entryRepository.undislike(any()) } returns false
            val entryId = "1"
            val entry =
                TimelineEntryModel(id = entryId, content = "", disliked = true, dislikesCount = 1)

            val res = sut(entry)

            assertNull(res)
            verifySuspend {
                entryRepository.undislike(entryId)
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.unfavorite(any())
            }
        }
}
