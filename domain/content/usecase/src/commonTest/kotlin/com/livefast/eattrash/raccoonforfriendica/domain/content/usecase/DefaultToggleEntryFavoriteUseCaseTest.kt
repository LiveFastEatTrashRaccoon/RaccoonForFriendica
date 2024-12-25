package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
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

class DefaultToggleEntryFavoriteUseCaseTest {
    private val entryRepository = mock<TimelineEntryRepository>()
    private val sut = DefaultToggleEntryFavoriteUseCase(entryRepository)

    @Test
    fun `given entry not favorite and not disliked when invoked then result and interactions are as expected`() =
        runTest {
            val entryId = "1"
            everySuspend { entryRepository.favorite(any()) } returns
                TimelineEntryModel(
                    id = entryId,
                    content = "",
                    favorite = true,
                    favoriteCount = 1,
                )
            val entry = TimelineEntryModel(id = entryId, content = "")

            val res = sut(entry)

            assertNotNull(res)
            assertEquals(1, res.favoriteCount)
            assertTrue(res.favorite)
            assertFalse(res.disliked)
            verifySuspend {
                entryRepository.favorite(entryId)
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.undislike(any())
            }
        }

    @Test
    fun `given entry not favorite and not disliked but favorite error when invoked then result and interactions are as expected`() =
        runTest {
            val entryId = "1"
            everySuspend { entryRepository.favorite(any()) } returns null
            val entry = TimelineEntryModel(id = entryId, content = "")

            val res = sut(entry)

            assertNull(res)
            verifySuspend {
                entryRepository.favorite(entryId)
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.undislike(any())
            }
        }

    @Test
    fun `given entry not favorite and disliked when invoked then result and interactions are as expected`() =
        runTest {
            val entryId = "1"
            everySuspend { entryRepository.favorite(any()) } returns
                TimelineEntryModel(
                    id = entryId,
                    content = "",
                    favorite = true,
                    favoriteCount = 1,
                )
            everySuspend { entryRepository.undislike(any()) } returns true
            val entry =
                TimelineEntryModel(id = entryId, content = "", disliked = true, dislikesCount = 1)

            val res = sut(entry)

            assertNotNull(res)
            assertEquals(1, res.favoriteCount)
            assertTrue(res.favorite)
            assertFalse(res.disliked)
            verifySuspend {
                entryRepository.favorite(entryId)
                entryRepository.undislike(entryId)
            }
        }

    @Test
    fun `given entry not favorite and disliked but favorite error when invoked then result and interactions are as expected`() =
        runTest {
            val entryId = "1"
            everySuspend { entryRepository.favorite(any()) } returns null
            everySuspend { entryRepository.undislike(any()) } returns true
            val entry =
                TimelineEntryModel(id = entryId, content = "", disliked = true, dislikesCount = 1)

            val res = sut(entry)

            assertNull(res)
            verifySuspend {
                entryRepository.favorite(entryId)
                entryRepository.undislike(entryId)
            }
        }

    @Test
    fun `given entry not favorite and disliked but dislike error when invoked then result and interactions are as expected`() =
        runTest {
            val entryId = "1"
            everySuspend { entryRepository.favorite(any()) } returns
                TimelineEntryModel(
                    id = entryId,
                    content = "",
                    favorite = true,
                    favoriteCount = 1,
                )
            everySuspend { entryRepository.undislike(any()) } returns false
            val entry =
                TimelineEntryModel(id = entryId, content = "", disliked = true, dislikesCount = 1)

            val res = sut(entry)

            assertNull(res)
            verifySuspend {
                entryRepository.undislike(entryId)
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.favorite(any())
            }
        }

    @Test
    fun `given entry favorite and not disliked when invoked then result and interactions are as expected`() =
        runTest {
            val entryId = "1"
            everySuspend { entryRepository.unfavorite(any()) } returns
                TimelineEntryModel(id = entryId, content = "")
            val entry =
                TimelineEntryModel(id = entryId, content = "", favorite = true, favoriteCount = 1)

            val res = sut(entry)

            assertNotNull(res)
            assertEquals(0, res.favoriteCount)
            assertFalse(res.favorite)
            assertFalse(res.disliked)
            verifySuspend {
                entryRepository.unfavorite(entryId)
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.undislike(any())
            }
        }

    @Test
    fun `given entry favorite and not disliked but favorite error when invoked then result and interactions are as expected`() =
        runTest {
            val entryId = "1"
            everySuspend { entryRepository.unfavorite(any()) } returns null
            val entry =
                TimelineEntryModel(id = entryId, content = "", favorite = true, favoriteCount = 1)

            val res = sut(entry)

            assertNull(res)
            verifySuspend {
                entryRepository.unfavorite(entryId)
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.undislike(any())
            }
        }
}
