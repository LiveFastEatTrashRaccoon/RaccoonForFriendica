package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PhotoService
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultPhotoRepositoryTest {
    private val photoService = mock<PhotoService>()
    private val serviceProvider = mock<ServiceProvider> { every { photo } returns photoService }
    private val sut = DefaultPhotoRepository(provider = serviceProvider)

    @Test
    fun `when create then result is as expected`() = runTest {
        val photo = FriendicaPhoto(id = "1")
        everySuspend { photoService.create(any()) } returns photo

        val bytes = byteArrayOf(0x00.toByte(), 0x01.toByte(), 0x00.toByte())
        val res =
            sut.create(
                bytes = bytes,
                album = "fake-album",
                alt = "fake-description",
            )

        assertEquals(photo.toModel(), res)
        verifySuspend {
            photoService.create(any())
        }
    }

    @Test
    fun `given error when create then result is as expected`() = runTest {
        everySuspend { photoService.create(any()) } throws IOException("Network error")

        val bytes = byteArrayOf(0x00.toByte(), 0x01.toByte(), 0x00.toByte())
        val res =
            sut.create(
                bytes = bytes,
                album = "fake-album",
                alt = "fake-description",
            )

        assertNull(res)
        verifySuspend {
            photoService.create(any())
        }
    }

    @Test
    fun `when update then result is as expected`() = runTest {
        everySuspend { photoService.update(content = any()) } returns
            FriendicaApiResult(result = "updated")

        val res =
            sut.update(
                id = "1",
                alt = "fake-description",
            )

        assertTrue(res)
        verifySuspend {
            photoService.update(content = any())
        }
    }

    @Test
    fun `given error when update then result is as expected`() = runTest {
        everySuspend { photoService.update(content = any()) } returns
            FriendicaApiResult(result = "ko")

        val res =
            sut.update(
                id = "1",
                alt = "fake-description",
            )

        assertFalse(res)
    }

    @Test
    fun `when delete then result is as expected`() = runTest {
        everySuspend { photoService.delete(content = any()) } returns
            FriendicaApiResult(result = "deleted")

        val res = sut.delete(id = "1")

        assertTrue(res)
        verifySuspend {
            photoService.delete(content = any())
        }
    }

    @Test
    fun `given error when delete then result is as expected`() = runTest {
        everySuspend { photoService.delete(content = any()) } returns
            FriendicaApiResult(result = "ko")

        val res = sut.delete(id = "1")

        assertFalse(res)
    }
}
