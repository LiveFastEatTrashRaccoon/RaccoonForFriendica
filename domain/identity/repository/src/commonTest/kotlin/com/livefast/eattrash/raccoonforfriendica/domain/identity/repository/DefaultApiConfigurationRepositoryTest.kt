package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import dev.mokkery.MockMode
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class DefaultApiConfigurationRepositoryTest {
    private val keyStore =
        mock<TemporaryKeyStore>(MockMode.autoUnit) {
            every { get("lastInstance", any<String>()) } returns "default-instance"
            every { get("lastMethod", any<String>()) } returns "OAuth2"
            every { get("lastCred1", any<String>()) } returns "fake-access-token"
            every { get("lastCred2", any<String>()) } returns ""
        }

    private val serviceProvider = mock<ServiceProvider>(MockMode.autoUnit)

    private val credentialsRepository =
        mock<CredentialsRepository> {
            everySuspend {
                validateApplicationCredentials(node = any(), credentials = any())
            } returns true
        }

    private val sut =
        DefaultApiConfigurationRepository(
            provider = serviceProvider,
            keyStore = keyStore,
            credentialsRepository = credentialsRepository,
        )

    @Test
    fun `when change node then interactions are as expected`() =
        runTest {
            sut.changeNode("new-instance")

            verify {
                serviceProvider.changeNode("new-instance")
                keyStore.save("lastInstance", "new-instance")
            }
        }

    @Test
    fun `when setAuth with OAuth credentials then interactions are as expected`() =
        runTest {
            val credentials =
                ApiCredentials.OAuth2(accessToken = "fake-access-token-2", refreshToken = "")
            sut.setAuth(credentials)

            assertTrue(sut.isLogged.value)
            verify {
                serviceProvider.setAuth(credentials.toServiceCredentials())
                keyStore.save("lastMethod", "OAuth2")
                keyStore.save("lastCred1", "fake-access-token-2")
                keyStore.save("lastCred2", "")
            }
        }

    @Test
    fun `when setAuth with basic credentials then interactions are as expected`() =
        runTest {
            val credentials = ApiCredentials.HttpBasic(user = "fake1", pass = "fake2")
            sut.setAuth(credentials)

            assertTrue(sut.isLogged.value)
            verify {
                serviceProvider.setAuth(credentials.toServiceCredentials())
                keyStore.save("lastMethod", "HTTPBasic")
                keyStore.save("lastCred1", "fake1")
                keyStore.save("lastCred2", "fake2")
            }
        }

    @Test
    fun `when setAuth with null credentials then interactions are as expected`() =
        runTest {
            sut.setAuth(null)

            assertFalse(sut.isLogged.value)
            verify {
                serviceProvider.setAuth()
                keyStore.remove("lastCred1")
                keyStore.remove("lastCred2")
            }
        }

    @Test
    fun `given invalid OAuth credentials stored when hasCachedAuthCredentials then result is as expected`() =
        runTest {
            everySuspend {
                credentialsRepository.validateApplicationCredentials(
                    node = any(),
                    credentials = any(),
                )
            } returns false

            val res = sut.hasCachedAuthCredentials()

            assertFalse(res)
            verifySuspend {
                credentialsRepository.validateApplicationCredentials(
                    "default-instance",
                    ApiCredentials.OAuth2(accessToken = "fake-access-token", refreshToken = ""),
                )
            }
        }

    @Test
    fun `given invalid basic credentials stored when hasCachedAuthCredentials then result is as expected`() =
        runTest {
            every { keyStore["lastMethod", any<String>()] } returns "HTTPBasic"
            every { keyStore["lastCred1", any<String>()] } returns "fake1"
            every { keyStore["lastCred2", any<String>()] } returns "fake2"
            everySuspend {
                credentialsRepository.validateApplicationCredentials(
                    node = any(),
                    credentials = any(),
                )
            } returns false

            val res = sut.hasCachedAuthCredentials()

            assertFalse(res)
            verifySuspend {
                credentialsRepository.validateApplicationCredentials(
                    "default-instance",
                    ApiCredentials.HttpBasic(user = "fake1", pass = "fake2"),
                )
            }
        }

    @Test
    fun `given timeout when hasCachedAuthCredentials then result is as expected`() =
        runTest {
            everySuspend {
                credentialsRepository.validateApplicationCredentials(
                    node = any(),
                    credentials = any(),
                )
            } calls {
                delay(10.seconds)
                true
            }

            val res = sut.hasCachedAuthCredentials()

            assertFalse(res)
            verifySuspend {
                credentialsRepository.validateApplicationCredentials(
                    "default-instance",
                    ApiCredentials.OAuth2(accessToken = "fake-access-token", refreshToken = ""),
                )
            }
        }

    @Test
    fun `given valid credentials when hasCachedAuthCredentials then result is as expected`() =
        runTest {
            val res = sut.hasCachedAuthCredentials()

            assertTrue(res)
            verifySuspend {
                credentialsRepository.validateApplicationCredentials(
                    "default-instance",
                    ApiCredentials.OAuth2(accessToken = "fake-access-token", refreshToken = ""),
                )
            }
        }
}
