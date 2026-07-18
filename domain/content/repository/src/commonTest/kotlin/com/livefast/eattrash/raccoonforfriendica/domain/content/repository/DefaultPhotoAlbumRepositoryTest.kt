package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PhotoAlbumService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PhotoService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DefaultPhotoAlbumRepositoryTest {
    private val photoService = mock<PhotoService>()
    private val photoAlbumService = mock<PhotoAlbumService>()
    private val serviceProvider =
        mock<ServiceProvider> {
            every { photo } returns photoService
            every { photoAlbum } returns photoAlbumService
        }
    private val sut = DefaultPhotoAlbumRepository(provider = serviceProvider)

    @Test
    fun `when getAll then result is as expected`() = runTest {
        val list = listOf(
            FriendicaPhoto(id = "id-1", album = "fake-album-1"),
            FriendicaPhoto(id = "id-2", album = "fake-album-1"),
            FriendicaPhoto(id = "id-3", album = "fake-album-2"),
        )
        everySuspend { photoService.getAll() } returns list

        val res = sut.getAll()

        assertNotNull(res)
        assertEquals(2, res.size)
        assertEquals(MediaAlbumModel(name = "fake-album-1", items = 2), res[0])
        assertEquals(MediaAlbumModel(name = "fake-album-2", items = 1), res[1])
        verifySuspend {
            photoService.getAll()
        }
    }

    @Test
    fun `when update then result is as expected`() = runTest {
        everySuspend { photoAlbumService.update(any()) } returns
            FriendicaApiResult(result = "updated")

        val res = sut.update(oldName = "fake-album", newName = "fake-album-new")

        assertTrue(res)
        verifySuspend {
            photoAlbumService.update(
                matches {
                    it.formData["album"] == "fake-album" && it.formData["album_new"] == "fake-album-new"
                },
            )
        }
    }

    @Test
    fun `given error when update then result is as expected`() = runTest {
        everySuspend { photoAlbumService.update(any()) } returns
            FriendicaApiResult(result = "ko")

        val res = sut.update(oldName = "fake-album", newName = "fake-album-new")

        assertFalse(res)
    }

    @Test
    fun `when delete then result is as expected`() = runTest {
        everySuspend { photoAlbumService.delete(any()) } returns
            FriendicaApiResult(result = "deleted")

        val res = sut.delete(name = "fake-album")

        assertTrue(res)
        verifySuspend {
            photoAlbumService.delete(
                matches {
                    it.formData["album"] == "fake-album"
                },
            )
        }
    }

    @Test
    fun `given error when delete then result is as expected`() = runTest {
        everySuspend { photoAlbumService.delete(any()) } returns
            FriendicaApiResult(result = "ko")

        val res = sut.delete(name = "fake-album")

        assertFalse(res)
    }

    @Test
    fun `given first page when getPhotos then result is as expected`() = runTest {
        val list = listOf(
            FriendicaPhoto(id = "id-1", album = "fake-album-1"),
            FriendicaPhoto(id = "id-2", album = "fake-album-1"),
            FriendicaPhoto(id = "id-3", album = "fake-album-2"),
        )
        everySuspend {
            photoService.getAll()
        } returns list

        val res = sut.getPhotos(album = "fake-album-1", pageCursor = null)

        assertEquals(list.subList(0, 2).map { it.toModel() }, res)
        verifySuspend {
            photoService.getAll()
        }
    }

    @Test
    fun `given other page when getPhotos then result is as expected`() = runTest {
        val list = listOf(
            FriendicaPhoto(id = "id-1", album = "fake-album-1"),
            FriendicaPhoto(id = "id-2", album = "fake-album-1"),
            FriendicaPhoto(id = "id-3", album = "fake-album-2"),
        )
        everySuspend {
            photoService.getAll()
        } returns list

        val res = sut.getPhotos(album = "fake-album-1", pageCursor = "id-2")

        assertNotNull(res)
        assertEquals(0, res.size)
        verifySuspend(VerifyMode.not) {
            photoService.getAll()
        }
    }
}
