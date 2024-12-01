package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaAttachment
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.MediaService
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultMediaRepositoryTest {
    private val mediaService = mock<MediaService>()
    private val serviceProvider = mock<ServiceProvider> { every { media } returns mediaService }
    private val sut = DefaultMediaRepository(provider = serviceProvider)

    @Test
    fun `when getBy then result is as expected`() =
        runTest {
            val attachment =
                MediaAttachment(
                    id = "1",
                    type = MediaType.IMAGE,
                )
            everySuspend { mediaService.getBy(any()) } returns attachment

            val res = sut.getBy("1")

            assertEquals(attachment.toModel(), res)
            verifySuspend {
                mediaService.getBy("1")
            }
        }

    @Test
    fun `when create then result is as expected`() =
        runTest {
            val attachment =
                MediaAttachment(
                    id = "1",
                    type = MediaType.IMAGE,
                )
            everySuspend { mediaService.create(any()) } returns attachment

            val bytes = byteArrayOf(0x00.toByte(), 0x01.toByte(), 0x00.toByte())
            val res =
                sut.create(
                    bytes = bytes,
                    album = "fake-album",
                    alt = "fake-description",
                )

            assertEquals(attachment.toModel(), res)
            verifySuspend {
                mediaService.create(any())
            }
        }

    @Test
    fun `when update then result is as expected`() =
        runTest {
            val attachment =
                MediaAttachment(
                    id = "1",
                    type = MediaType.IMAGE,
                )
            everySuspend { mediaService.update(id = any(), content = any()) } returns attachment

            val res =
                sut.update(
                    id = "1",
                    alt = "fake-description",
                )

            assertTrue(res)
            verifySuspend {
                mediaService.update(id = any(), content = any())
            }
        }
}
