package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultApiConfigurationRepositoryTest {
    private val keyStore =
        mock<TemporaryKeyStore>(MockMode.autoUnit) {
            everySuspend { get("lastInstance", any<String>()) } returns "default-instance"
            everySuspend { get("lastMethod", any<String>()) } returns "OAuth2"
            everySuspend { get("lastCred1", any<String>()) } returns "fake-access-token"
            everySuspend { get("lastCred2", any<String>()) } returns ""
        }

    private val serviceProvider = mock<ServiceProvider>(MockMode.autoUnit)

    private val credentialsRepository =
        mock<CredentialsRepository> {
            everySuspend {
                validateApplicationCredentials(node = any(), credentials = any())
            } returns true
        }

    private val authManager = mock<AuthManager>()

    private val sut =
        DefaultApiConfigurationRepository(
            provider = serviceProvider,
            keyStore = keyStore,
            credentialsRepository = credentialsRepository,
            authManager = authManager,
        )

    // region changeNode
    @Test
    fun `when changeNode then interactions are as expected`() = runTest {
        sut.changeNode("new-instance")

        verifySuspend {
            serviceProvider.changeNode("new-instance")
            keyStore.save("lastInstance", "new-instance")
        }
    }
    // endregion

    // region setAuth
    @Test
    fun `when setAuth with OAuth credentials then interactions are as expected`() = runTest {
        val credentials = ApiCredentials.OAuth2(accessToken = "fake-access-token-2", refreshToken = "")
        sut.setAuth(credentials)

        assertTrue(sut.isLogged.value)
        verifySuspend {
            serviceProvider.setAuth(credentials.toServiceCredentials())
            keyStore.save("lastMethod", "OAuth2")
            keyStore.save("lastCred1", "fake-access-token-2")
            keyStore.save("lastCred2", "")
        }
    }

    @Test
    fun `when setAuth with basic credentials then interactions are as expected`() = runTest {
        val credentials = ApiCredentials.HttpBasic(user = "fake1", pass = "fake2")
        sut.setAuth(credentials)

        assertTrue(sut.isLogged.value)
        verifySuspend {
            serviceProvider.setAuth(credentials.toServiceCredentials())
            keyStore.save("lastMethod", "HTTPBasic")
            keyStore.save("lastCred1", "fake1")
            keyStore.save("lastCred2", "fake2")
        }
    }

    @Test
    fun `when setAuth with null credentials then interactions are as expected`() = runTest {
        sut.setAuth(null)

        assertFalse(sut.isLogged.value)
        verifySuspend {
            serviceProvider.setAuth()
            keyStore.remove("lastCred1")
            keyStore.remove("lastCred2")
        }
    }
    // endregion

    // region hasCachedAuthCredentials
    @Test
    fun `given no credentials stored when hasCachedAuthCredentials then result is false`() = runTest {
        everySuspend { keyStore.get("lastCred1", any<String>()) } returns ""

        val res = sut.hasCachedAuthCredentials()

        assertFalse(res)
    }

    @Test
    fun `given stored credentials when hasCachedAuthCredentials then result is true`() = runTest {
        val res = sut.hasCachedAuthCredentials()

        assertTrue(res)
    }
    // endregion

    // region refresh
    @Test
    fun `given previous OAuth2 credentials when refresh then result is success`() = runTest {
        val refreshToken = "fake-refresh-token"
        everySuspend { keyStore.get("lastCred2", any<String>()) } returns refreshToken
        everySuspend {
            authManager.performRefresh(any())
        } returns ApiCredentials.OAuth2(accessToken = "fake-access-token-2", refreshToken = refreshToken)
        val credentials = ApiCredentials.OAuth2(accessToken = "fake-access-token-1", refreshToken = refreshToken)
        sut.setAuth(credentials)

        val res = sut.refresh()

        assertTrue(res.isSuccess)
        verifySuspend {
            authManager.performRefresh(refreshToken)
        }
    }

    @Test
    fun `given no previous OAuth2 credentials when refresh then result is failure`() = runTest {
        everySuspend { keyStore.get("lastCred1", "") } returns ""
        everySuspend { keyStore.get("lastCred2", "") } returns ""
        val credentials =
            ApiCredentials.OAuth2(accessToken = "fake-access-token-1", refreshToken = "fake-refresh-token")
        sut.setAuth(credentials)

        val res = sut.refresh()

        assertTrue(res.isFailure)
    }

    @Test
    fun `given failure when refresh then result is failure`() = runTest {
        val refreshToken = "fake-refresh-token"
        everySuspend { keyStore.get("lastCred2", any<String>()) } returns refreshToken
        everySuspend { authManager.performRefresh(any()) } returns null
        val credentials = ApiCredentials.OAuth2(accessToken = "fake-access-token-1", refreshToken = refreshToken)
        sut.setAuth(credentials)

        val res = sut.refresh()

        assertTrue(res.isFailure)
        verifySuspend {
            authManager.performRefresh(refreshToken)
        }
    }
    // endregion
}
