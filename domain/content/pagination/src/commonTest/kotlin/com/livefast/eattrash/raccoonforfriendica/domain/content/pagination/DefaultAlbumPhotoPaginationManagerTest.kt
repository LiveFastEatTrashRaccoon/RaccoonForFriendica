package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
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

class DefaultAlbumPhotoPaginationManagerTest {
    private val albumRepository = mock<PhotoAlbumRepository>()
    private val sut = DefaultAlbumPhotoPaginationManager(albumRepository = albumRepository)

    @Test
    fun `given no photos when loadNextPage then result is as expected`() =
        runTest {
            everySuspend { albumRepository.getPhotos(any(), any(), any()) } returns emptyList()

            sut.reset(AlbumPhotoPaginationSpecification.Default(album = ALBUM_NAME))
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                albumRepository.getPhotos(
                    album = ALBUM_NAME,
                    pageCursor = null,
                    latestFirst = true,
                )
            }
        }

    @Test
    fun `given photos when loadNextPage then result is as expected`() =
        runTest {
            val elements = listOf(AttachmentModel(id = "1", url = ""))
            everySuspend { albumRepository.getPhotos(any(), any(), any()) } returns elements

            sut.reset(AlbumPhotoPaginationSpecification.Default(album = ALBUM_NAME))
            val res = sut.loadNextPage()

            assertEquals(elements, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                albumRepository.getPhotos(
                    album = ALBUM_NAME,
                    pageCursor = null,
                    latestFirst = true,
                )
            }
        }

    @Test
    fun `given can not fetch more when loadNextPage twice then result is as expected`() =
        runTest {
            val elements = listOf(AttachmentModel(id = "1", url = ""))
            everySuspend {
                albumRepository.getPhotos(
                    any(),
                    any(),
                    any(),
                )
            } sequentiallyReturns
                listOf(
                    elements,
                    emptyList(),
                )

            sut.reset(AlbumPhotoPaginationSpecification.Default(album = ALBUM_NAME))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(elements, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                albumRepository.getPhotos(
                    album = ALBUM_NAME,
                    pageCursor = null,
                    latestFirst = true,
                )
                albumRepository.getPhotos(
                    album = ALBUM_NAME,
                    pageCursor = "1",
                    latestFirst = true,
                )
            }
        }

    companion object {
        private const val ALBUM_NAME = "fake-album"
    }
}
