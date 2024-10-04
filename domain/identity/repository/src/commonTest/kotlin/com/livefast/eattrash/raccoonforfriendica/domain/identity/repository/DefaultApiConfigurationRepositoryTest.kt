package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.TemporaryKeyStore
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
                validateApplicationCredentials(any(), any())
            } returns true
        }

    private val repository =
        DefaultApiConfigurationRepository(
            serviceProvider = serviceProvider,
            keyStore = keyStore,
            credentialsRepository = credentialsRepository,
        )

    @Test
    fun `given valid credentials stored when initialize then is logged`() =
        runTest {
            repository.initialize()

            assertTrue(repository.isLogged.value)
            verify {
                serviceProvider.setAuth(any())
                serviceProvider.changeNode("default-instance")
            }
        }

    @Test
    fun `given expired credentials stored when initialize then is not logged`() =
        runTest {
            everySuspend {
                credentialsRepository.validateApplicationCredentials(any(), any())
            } returns false

            repository.initialize()

            assertFalse(repository.isLogged.value)
            verify(mode = VerifyMode.not) {
                serviceProvider.setAuth()
            }
        }

    @Test
    fun `when change node then interactions are as expected`() =
        runTest {
            repository.changeNode("new-instance")

            verify {
                serviceProvider.changeNode("new-instance")
                keyStore.save("lastInstance", "new-instance")
            }
        }

    @Test
    fun `when setAuth then interactions are as expected`() =
        runTest {
            val credentials = ApiCredentials.OAuth2("fake-access-token-2", "")
            repository.setAuth(credentials)

            assertTrue(repository.isLogged.value)
            verify {
                serviceProvider.setAuth(credentials.toServiceCredentials())
                keyStore.save("lastMethod", "OAuth2")
                keyStore.save("lastCred1", "fake-access-token-2")
                keyStore.save("lastCred2", "")
            }
        }

    @Test
    fun `when setAuth with null credentials then interactions are as expected`() =
        runTest {
            repository.setAuth(null)

            assertFalse(repository.isLogged.value)
            verify {
                serviceProvider.setAuth()
                keyStore.remove("lastCred1")
                keyStore.remove("lastCred2")
            }
        }

    @Test
    fun `given invalid credentials stored when hasCachedAuthCredentials then result is as expected`() =
        runTest {
            everySuspend {
                credentialsRepository.validateApplicationCredentials(any(), any())
            } returns false

            val res = repository.hasCachedAuthCredentials()

            assertFalse(res)
            verifySuspend {
                credentialsRepository.validateApplicationCredentials(
                    "default-instance",
                    ApiCredentials.OAuth2("fake-access-token", ""),
                )
            }
        }
}
