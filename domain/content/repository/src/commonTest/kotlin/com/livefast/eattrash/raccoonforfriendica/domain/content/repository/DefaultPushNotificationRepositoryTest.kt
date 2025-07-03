package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.PushSubscription
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PushService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultPushNotificationRepositoryTest {
    private val pushService = mock<PushService>()
    private val serviceProvider =
        mock<ServiceProvider> {
            every { push } returns pushService
        }
    private val sut = DefaultPushNotificationRepository(provider = serviceProvider)

    @Test
    fun `when create then result is as expected`() = runTest {
        everySuspend { pushService.create(any()) } returns
            PushSubscription(
                id = "1",
                endpoint = ENDPOINT,
                serverKey = SERVER_KEY,
            )

        val res =
            sut.create(
                endpoint = ENDPOINT,
                pubKey = PUB_KEY,
                auth = AUTH,
                types = NotificationType.ALL,
                policy = NotificationPolicy.All,
            )

        assertEquals(SERVER_KEY, res)
        verifySuspend {
            pushService.create(
                matching {
                    val data = it.formData
                    listOf(
                        data["subscription[endpoint]"] == ENDPOINT,
                        data["subscription[keys][p256dh]"] == PUB_KEY,
                        data["subscription[keys][auth]"] == AUTH,
                        data["data[alerts][status]"] == "true",
                        data["data[alerts][favourite]"] == "true",
                        data["data[alerts][follow]"] == "true",
                        data["data[alerts][follow_request]"] == "true",
                        data["data[alerts][mention]"] == "true",
                        data["data[alerts][poll]"] == "true",
                        data["data[alerts][reblog]"] == "true",
                        data["data[alerts][update]"] == "true",
                        data["data[policy]"] == "all",
                    ).all { condition -> condition }
                },
            )
        }
    }

    @Test
    fun `given error when create then result is as expected`() = runTest {
        everySuspend { pushService.create(any()) } throws IOException("Network error")

        val res =
            sut.create(
                endpoint = ENDPOINT,
                pubKey = PUB_KEY,
                auth = AUTH,
                types = NotificationType.ALL,
                policy = NotificationPolicy.All,
            )

        assertNull(res)
        verifySuspend {
            pushService.create(
                matching {
                    val data = it.formData
                    listOf(
                        data["subscription[endpoint]"] == ENDPOINT,
                        data["subscription[keys][p256dh]"] == PUB_KEY,
                        data["subscription[keys][auth]"] == AUTH,
                        data["data[alerts][status]"] == "true",
                        data["data[alerts][favourite]"] == "true",
                        data["data[alerts][follow]"] == "true",
                        data["data[alerts][follow_request]"] == "true",
                        data["data[alerts][mention]"] == "true",
                        data["data[alerts][poll]"] == "true",
                        data["data[alerts][reblog]"] == "true",
                        data["data[alerts][update]"] == "true",
                        data["data[policy]"] == "all",
                    ).all { condition -> condition }
                },
            )
        }
    }

    @Test
    fun `when update then result is as expected`() = runTest {
        everySuspend { pushService.update(any()) } returns
            PushSubscription(
                id = "1",
                endpoint = ENDPOINT,
                serverKey = SERVER_KEY,
            )

        val res =
            sut.update(
                types = NotificationType.ALL,
                policy = NotificationPolicy.All,
            )

        assertEquals(SERVER_KEY, res)
        verifySuspend {
            pushService.update(
                matching {
                    val data = it.formData
                    listOf(
                        data.names().none { k -> k.startsWith("subscription") },
                        data["data[alerts][status]"] == "true",
                        data["data[alerts][favourite]"] == "true",
                        data["data[alerts][follow]"] == "true",
                        data["data[alerts][follow_request]"] == "true",
                        data["data[alerts][mention]"] == "true",
                        data["data[alerts][poll]"] == "true",
                        data["data[alerts][reblog]"] == "true",
                        data["data[alerts][update]"] == "true",
                        data["data[policy]"] == "all",
                    ).all { condition -> condition }
                },
            )
        }
    }

    @Test
    fun `given error when update then result is as expected`() = runTest {
        everySuspend { pushService.update(any()) } throws IOException("Network error")
        val res =
            sut.update(
                types = NotificationType.ALL,
                policy = NotificationPolicy.All,
            )

        assertNull(res)
        verifySuspend {
            pushService.update(
                matching {
                    val data = it.formData
                    listOf(
                        data.names().none { k -> k.startsWith("subscription") },
                        data["data[alerts][status]"] == "true",
                        data["data[alerts][favourite]"] == "true",
                        data["data[alerts][follow]"] == "true",
                        data["data[alerts][follow_request]"] == "true",
                        data["data[alerts][mention]"] == "true",
                        data["data[alerts][poll]"] == "true",
                        data["data[alerts][reblog]"] == "true",
                        data["data[alerts][update]"] == "true",
                        data["data[policy]"] == "all",
                    ).all { condition -> condition }
                },
            )
        }
    }

    @Test
    fun `when delete then result is as expected`() = runTest {
        everySuspend { pushService.delete() } returns true

        val res = sut.delete()

        assertTrue(res)
        verifySuspend {
            pushService.delete()
        }
    }

    @Test
    fun `given error when delete then result is as expected`() = runTest {
        everySuspend { pushService.delete() } throws IOException("Network error")

        val res = sut.delete()

        assertFalse(res)
        verifySuspend {
            pushService.delete()
        }
    }

    companion object {
        private const val SERVER_KEY = "fake-server-key"
        private const val ENDPOINT = "fake-server-key"
        private const val PUB_KEY = "fake-pub-key"
        private const val AUTH = "fake-token"
    }
}
