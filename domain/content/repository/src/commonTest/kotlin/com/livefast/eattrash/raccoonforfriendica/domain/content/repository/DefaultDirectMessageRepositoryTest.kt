package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPrivateMessage
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DirectMessageService
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
import kotlin.test.assertTrue

class DefaultDirectMessageRepositoryTest {
    private val messageService = mock<DirectMessageService>()
    private val serviceProvider =
        mock<ServiceProvider> {
            every { directMessage } returns messageService
        }
    private val sut = DefaultDirectMessageRepository(provider = serviceProvider)

    @Test
    fun `when getAll then result is as expected`() = runTest {
        val list = listOf(FriendicaPrivateMessage(id = 1))
        everySuspend {
            messageService.getAll(
                page = any(),
                count = any(),
                maxId = any(),
                getText = any(),
            )
        } returns list

        val res = sut.getAll(page = 1)

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend {
            messageService.getAll(
                page = 1,
                count = 20,
                maxId = null,
                getText = "html",
            )
        }
    }

    @Test
    fun `when getReplies then result is as expected`() = runTest {
        val list = listOf(FriendicaPrivateMessage(id = 1))
        everySuspend {
            messageService.getConversation(
                uri = any(),
                page = any(),
                count = any(),
                maxId = any(),
                sinceId = any(),
                getText = any(),
            )
        } returns list

        val res =
            sut.getReplies(
                parentUri = "uri",
                page = 1,
            )

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend {
            messageService.getConversation(
                uri = "uri",
                page = 1,
                count = 20,
                maxId = null,
                getText = "html",
                sinceId = null,
            )
        }
    }

    @Test
    fun `when pollReplies then result is as expected`() = runTest {
        val list = listOf(FriendicaPrivateMessage(id = 1))
        everySuspend {
            messageService.getConversation(
                uri = any(),
                page = any(),
                count = any(),
                maxId = any(),
                sinceId = any(),
                getText = any(),
            )
        } returns list

        val res =
            sut.pollReplies(
                parentUri = "uri",
                minId = "1",
            )

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend {
            messageService.getConversation(
                uri = "uri",
                page = 1,
                count = 20,
                maxId = null,
                getText = "html",
                sinceId = 1,
            )
        }
    }

    @Test
    fun `when create then result is as expected`() = runTest {
        val text = "text"
        val recipientId = "recipient-id"
        val message = FriendicaPrivateMessage(id = 1, text = text)
        everySuspend {
            messageService.create(any())
        } returns message

        val res =
            sut.create(
                recipientId = recipientId,
                text = text,
            )

        assertEquals(message.toModel(), res)
        verifySuspend {
            messageService.create(
                matching {
                    it.formData.let { data ->
                        data["text"] == text && data["user_id"] == recipientId
                    }
                },
            )
        }
    }

    @Test
    fun `when delete then result is as expected`() = runTest {
        everySuspend {
            messageService.delete(any())
        } returns FriendicaApiResult(result = "ok")

        val res = sut.delete("1")

        assertTrue(res)
        verifySuspend {
            messageService.delete(1)
        }
    }

    @Test
    fun `when markAsRead then result is as expected`() = runTest {
        everySuspend {
            messageService.markAsRead(any())
        } returns FriendicaApiResult(result = "ok")

        val res = sut.markAsRead("1")

        assertTrue(res)
        verifySuspend {
            messageService.markAsRead(1)
        }
    }
}
