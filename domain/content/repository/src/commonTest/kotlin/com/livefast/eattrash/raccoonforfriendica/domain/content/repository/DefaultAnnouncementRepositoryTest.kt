package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Announcement
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.AnnouncementService
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultAnnouncementRepositoryTest {
    private val announcementService = mock<AnnouncementService>()
    private val serviceProvider =
        mock<ServiceProvider> {
            every { announcements } returns announcementService
        }
    private val sut = DefaultAnnouncementRepository(provider = serviceProvider)

    @Test
    fun `when getAll then result is as expected`() =
        runTest {
            val list = listOf(Announcement(id = "1", content = "lorem ipsum"))
            everySuspend { announcementService.getAll() } returns list

            val res = sut.getAll()

            assertEquals(list.map { it.toModel() }, res)
            verifySuspend {
                announcementService.getAll()
            }
        }

    @Test
    fun `when getAll twice with no refresh then result is as expected`() =
        runTest {
            val list = listOf(Announcement(id = "1", content = "lorem ipsum"))
            everySuspend { announcementService.getAll() } returns list

            sut.getAll()
            val res = sut.getAll()

            assertEquals(list.map { it.toModel() }, res)
            verifySuspend(VerifyMode.exactly(1)) {
                announcementService.getAll()
            }
        }

    @Test
    fun `when getAll twice with refresh then result is as expected`() =
        runTest {
            val list = listOf(Announcement(id = "1", content = "lorem ipsum"))
            everySuspend { announcementService.getAll() } returns list

            sut.getAll()
            val res = sut.getAll(refresh = true)

            assertEquals(list.map { it.toModel() }, res)
            verifySuspend(VerifyMode.exactly(2)) {
                announcementService.getAll()
            }
        }

    @Test
    fun `given success when markAsRead then result is as expected`() =
        runTest {
            everySuspend { announcementService.dismiss(any()) } returns mockResponse(res = Unit)

            val res = sut.markAsRead(id = "1")

            assertTrue(res)
            verifySuspend {
                announcementService.dismiss("1")
            }
        }

    @Test
    fun `given error when markAsRead then result is as expected`() =
        runTest {
            everySuspend { announcementService.dismiss(any()) } returns
                mockResponse(statusCode = HttpStatusCode.InternalServerError)

            val res = sut.markAsRead(id = "1")

            assertFalse(res)
            verifySuspend {
                announcementService.dismiss("1")
            }
        }

    @Test
    fun `given success when addReaction then result is as expected`() =
        runTest {
            everySuspend {
                announcementService.addReaction(
                    id = any(),
                    name = any(),
                )
            } returns mockResponse(res = Unit)

            val res = sut.addReaction(id = "1", reaction = "🦝")

            assertTrue(res)
            verifySuspend {
                announcementService.addReaction("1", name = "🦝")
            }
        }

    @Test
    fun `given error when addReaction then result is as expected`() =
        runTest {
            everySuspend {
                announcementService.addReaction(
                    id = any(),
                    name = any(),
                )
            } returns mockResponse(statusCode = HttpStatusCode.InternalServerError)

            val res = sut.addReaction(id = "1", reaction = "🦝")

            assertFalse(res)
            verifySuspend {
                announcementService.addReaction("1", name = "🦝")
            }
        }

    @Test
    fun `given success when removeReaction then result is as expected`() =
        runTest {
            everySuspend {
                announcementService.removeReaction(
                    id = any(),
                    name = any(),
                )
            } returns mockResponse(res = Unit)

            val res = sut.removeReaction(id = "1", reaction = "🦝")

            assertTrue(res)
            verifySuspend {
                announcementService.removeReaction("1", name = "🦝")
            }
        }

    @Test
    fun `given error when removeReaction then result is as expected`() =
        runTest {
            everySuspend {
                announcementService.removeReaction(
                    id = any(),
                    name = any(),
                )
            } returns mockResponse(statusCode = HttpStatusCode.InternalServerError)

            val res = sut.removeReaction(id = "1", reaction = "🦝")

            assertFalse(res)
            verifySuspend {
                announcementService.removeReaction("1", name = "🦝")
            }
        }
}
