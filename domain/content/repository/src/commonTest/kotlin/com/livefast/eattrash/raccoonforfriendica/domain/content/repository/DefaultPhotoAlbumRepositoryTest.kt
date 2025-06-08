package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhotoAlbum
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PhotoAlbumService
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultPhotoAlbumRepositoryTest {
    private val photoAlbumService = mock<PhotoAlbumService>()
    private val serviceProvider =
        mock<ServiceProvider> { every { photoAlbum } returns photoAlbumService }
    private val sut = DefaultPhotoAlbumRepository(provider = serviceProvider)

    @Test
    fun `when getAll then result is as expected`() = runTest {
        val list = listOf(FriendicaPhotoAlbum(name = "fake-album"))
        everySuspend { photoAlbumService.getAll() } returns list

        val res = sut.getAll()

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend {
            photoAlbumService.getAll()
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
                matching {
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
                matching {
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
    fun `when getPhotos then result is as expected`() = runTest {
        val list = listOf(FriendicaPhoto(id = "0"))
        everySuspend {
            photoAlbumService.getPhotos(
                album = any(),
                maxId = any(),
                latestFirst = any(),
                limit = any(),
            )
        } returns list

        val res = sut.getPhotos(album = "fake-album", pageCursor = null)

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend {
            photoAlbumService.getPhotos(
                album = "fake-album",
                maxId = null,
                latestFirst = false,
                limit = 20,
            )
        }
    }
}
