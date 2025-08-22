package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Notification
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.NotificationService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.NotificationType as NotificationTypeDto

class DefaultNotificationRepositoryTest {
    private val notificationService = mock<NotificationService>()
    private val serviceProvider =
        mock<ServiceProvider> { every { notification } returns notificationService }
    private val sut = DefaultNotificationRepository(provider = serviceProvider)

    @Test
    fun `given no results when getAll then result is as expected`() = runTest {
        everySuspend {
            notificationService.get(
                types = any(),
                excludeTypes = any(),
                maxId = any(),
                minId = any(),
                includeAll = any(),
                limit = any(),
            )
        } returns emptyList()

        val res = sut.getAll(types = NotificationType.ALL)

        assertEquals(emptyList(), res)
        verifySuspend {
            notificationService.get(
                types = matching { it.contains("mention") },
                excludeTypes = null,
                maxId = null,
                minId = null,
                includeAll = false,
                limit = 20,
            )
        }
    }

    @Test
    fun `when getAll then result is as expected`() = runTest {
        val list = listOf(Notification(id = "1", type = NotificationTypeDto.MENTION))
        everySuspend {
            notificationService.get(
                types = any(),
                excludeTypes = any(),
                maxId = any(),
                minId = any(),
                includeAll = any(),
                limit = any(),
            )
        } returns list

        val res = sut.getAll(types = NotificationType.ALL)

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend {
            notificationService.get(
                types = matching { it.contains("mention") },
                excludeTypes = null,
                maxId = null,
                minId = null,
                includeAll = false,
                limit = 20,
            )
        }
    }

    @Test
    fun `when getAll with page cursor then result is as expected`() = runTest {
        val list = listOf(Notification(id = "1", type = NotificationTypeDto.MENTION))
        everySuspend {
            notificationService.get(
                types = any(),
                excludeTypes = any(),
                maxId = any(),
                minId = any(),
                includeAll = any(),
                limit = any(),
            )
        } returns list

        val res = sut.getAll(types = NotificationType.ALL, pageCursor = "0")

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend {
            notificationService.get(
                types = matching { it.contains("mention") },
                excludeTypes = null,
                maxId = "0",
                minId = null,
                includeAll = false,
                limit = 20,
            )
        }
    }

    @Test
    fun `when getAll twice with no refresh then result is as expected`() = runTest {
        val list = listOf(Notification(id = "1", type = NotificationTypeDto.MENTION))
        everySuspend {
            notificationService.get(
                types = any(),
                excludeTypes = any(),
                maxId = any(),
                minId = any(),
                includeAll = any(),
                limit = any(),
            )
        } returns list

        sut.getAll(types = NotificationType.ALL)
        val res = sut.getAll(types = NotificationType.ALL)

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend(VerifyMode.exactly(1)) {
            notificationService.get(
                types = matching { it.contains("mention") },
                excludeTypes = null,
                maxId = null,
                minId = null,
                includeAll = false,
                limit = 20,
            )
        }
    }

    @Test
    fun `when getAll twice with refresh then result is as expected`() = runTest {
        val list = listOf(Notification(id = "1", type = NotificationTypeDto.MENTION))
        everySuspend {
            notificationService.get(
                types = any(),
                excludeTypes = any(),
                maxId = any(),
                minId = any(),
                includeAll = any(),
                limit = any(),
            )
        } returns list

        sut.getAll(types = NotificationType.ALL)
        val res = sut.getAll(types = NotificationType.ALL, refresh = true)

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend(VerifyMode.exactly(2)) {
            notificationService.get(
                types = matching { it.contains("mention") },
                excludeTypes = null,
                maxId = null,
                minId = null,
                includeAll = false,
                limit = 20,
            )
        }
    }

    @Test
    fun `when dismiss then result is as expected`() = runTest {
        everySuspend { notificationService.dismiss(any()) } returns true

        val res = sut.dismiss("1")

        assertTrue(res)
        verifySuspend {
            notificationService.dismiss("1")
        }
    }

    @Test
    fun `given error when dismiss then result is as expected`() = runTest {
        everySuspend { notificationService.dismiss(any()) } returns false

        val res = sut.dismiss("1")

        assertFalse(res)
        verifySuspend {
            notificationService.dismiss("1")
        }
    }

    @Test
    fun `when dismissAll then result is as expected`() = runTest {
        everySuspend { notificationService.clear() } returns true

        val res = sut.dismissAll()

        assertTrue(res)
        verifySuspend {
            notificationService.clear()
        }
    }

    @Test
    fun `given error when dismissAll then result is as expected`() = runTest {
        everySuspend { notificationService.clear() } returns false

        val res = sut.dismissAll()

        assertFalse(res)
        verifySuspend {
            notificationService.clear()
        }
    }
}
