package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaAttachment
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.IMAGE
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.MediaService
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DraftDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.DraftEntity
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import dev.mokkery.MockMode
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultDraftRepositoryTest {
    private val draftDao = mock<DraftDao>(MockMode.autoUnit)
    private val mediaService =
        mock<MediaService> {
            everySuspend { getBy(any()) } calls {
                val id = it.args.first().toString()
                MediaAttachment(
                    id = id,
                    type = IMAGE,
                    url = "fake-url-$id",
                )
            }
        }
    private val serviceProvider = mock<ServiceProvider> { every { media } returns mediaService }
    private val sut =
        DefaultDraftRepository(
            draftDao = draftDao,
            provider = serviceProvider,
        )

    @Test
    fun `when getAll then result is as expected`() =
        runTest {
            everySuspend {
                draftDao.getAll(offset = any(), limit = any())
            } returns listOf(DraftEntity(id = "1", text = "text"))

            val res = sut.getAll(page = 0)
            assertEquals(listOf(TimelineEntryModel(id = "1", content = "text")), res)
            verifySuspend {
                draftDao.getAll(offset = 0, limit = 20)
            }
        }

    @Test
    fun `given elements with attachments when getAll then result is as expected`() =
        runTest {
            everySuspend {
                draftDao.getAll(offset = any(), limit = any())
            } returns listOf(DraftEntity(id = "1", text = "text", mediaIds = "2,3"))

            val res = sut.getAll(page = 0)
            assertEquals(
                listOf(
                    TimelineEntryModel(
                        id = "1",
                        content = "text",
                        attachments =
                            listOf(
                                AttachmentModel(
                                    id = "2",
                                    type = MediaType.Image,
                                    url = "fake-url-2",
                                ),
                                AttachmentModel(
                                    id = "3",
                                    type = MediaType.Image,
                                    url = "fake-url-3",
                                ),
                            ),
                    ),
                ),
                res,
            )
            verifySuspend {
                draftDao.getAll(offset = 0, limit = 20)
                mediaService.getBy("2")
                mediaService.getBy("3")
            }
        }

    @Test
    fun `when getById then result is as expected`() =
        runTest {
            everySuspend {
                draftDao.getBy(any())
            } returns DraftEntity(id = "1", text = "text")

            val res = sut.getById(id = "1")
            assertEquals(TimelineEntryModel(id = "1", content = "text"), res)
            verifySuspend {
                draftDao.getBy(id = "1")
            }
        }

    @Test
    fun `when create then result is as expected`() =
        runTest {
            val entity =
                DraftEntity(
                    id = "1",
                    text = "text",
                    mediaIds = "",
                    sensitive = false,
                    visibility = "public",
                )
            everySuspend { draftDao.getBy(any()) } returns entity
            val entry = TimelineEntryModel(id = "1", content = "text")
            val res = sut.create(entry)

            assertEquals(entry, res)
            verifySuspend {
                draftDao.insert(entity)
            }
        }

    @Test
    fun `when update then result is as expected`() =
        runTest {
            val entity =
                DraftEntity(
                    id = "1",
                    text = "text",
                    mediaIds = "",
                    sensitive = false,
                    visibility = "public",
                )
            everySuspend { draftDao.getBy(any()) } returns entity

            val entry = TimelineEntryModel(id = "1", content = "text")
            val res = sut.update(entry)

            assertEquals(entry, res)
            verifySuspend {
                draftDao.update(entity)
            }
        }

    @Test
    fun `when delete then result is as expected`() =
        runTest {
            val res = sut.delete(id = "1")

            assertTrue(res)
            verifySuspend {
                draftDao.delete(DraftEntity(id = "1"))
            }
        }

    @Test
    fun `given error when delete then result is as expected`() =
        runTest {
            everySuspend { draftDao.delete(any()) } throws IllegalStateException()

            val res = sut.delete(id = "1")

            assertFalse(res)
            verifySuspend {
                draftDao.delete(DraftEntity(id = "1"))
            }
        }
}
