package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Application
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Instance
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.AppService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.InstanceService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.UserService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.ClientApplicationModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultCredentialsRepositoryTest {
    private val engine =
        mock<HttpClientEngine>(mode = MockMode.autofill) {
            every { coroutineContext } returns TestScope().coroutineContext
        }
    private val instanceService = mock<InstanceService>(mode = MockMode.autoUnit)
    private val userService = mock<UserService>(mode = MockMode.autoUnit)
    private val appService = mock<AppService>(mode = MockMode.autoUnit)
    private val provider =
        mock<ServiceProvider>(mode = MockMode.autoUnit) {
            every { instance } returns instanceService
            every { users } returns userService
            every { apps } returns appService
        }

    private val sut =
        DefaultCredentialsRepository(
            provider = provider,
            engine = engine,
        )

    @Test
    fun `given timeline can be retrieved when validateNode then result is as expected`() = runTest {
        everySuspend { instanceService.getInfo() } returns Instance(domain = "example.com")
        val node = "test-node"

        val res = sut.validateNode(node)

        assertTrue(res)
        verifySuspend {
            provider.changeNode(node)
            instanceService.getInfo()
        }
    }

    @Test
    fun `given timeline can not be retrieved when validateNode then result is as expected`() = runTest {
        everySuspend { instanceService.getInfo() } throws
            IOException("Network call was not successful")
        val node = "test-node"

        val res = sut.validateNode(node)

        assertFalse(res)
        verifySuspend {
            provider.changeNode(node)
            instanceService.getInfo()
        }
    }

    @Test
    fun `given valid credentials when validate credentials then result is as expected`() = runTest {
        val credentials = ApiCredentials.OAuth2("fake-access-token", "")
        everySuspend { userService.verifyCredentials() } returns
            CredentialAccount(
                id = "fake-user-id",
                acct = "fake-username",
                username = "fake-username",
            )
        val node = "test-node"
        val user =
            UserModel(
                id = "fake-user-id",
                handle = "fake-username",
                username = "fake-username",
            )

        val res = sut.validate(node = node, credentials = credentials)

        assertEquals(res, user)
        verifySuspend {
            provider.changeNode(node)
            provider.setAuth(credentials.toServiceCredentials())
            userService.verifyCredentials()
        }
    }

    @Test
    fun `given invalid credentials when validate credentials then result is as expected`() = runTest {
        val credentials = ApiCredentials.OAuth2("fake-access-token", "")
        everySuspend { userService.verifyCredentials() } throws IOException("Invalid credentials")
        val node = "test-node"

        val res = sut.validate(node = node, credentials = credentials)

        assertNull(res)
        verifySuspend {
            provider.changeNode(node)
            provider.setAuth(credentials.toServiceCredentials())
            userService.verifyCredentials()
        }
    }

    @Test
    fun `given network error when createApplication then result is as expected`() = runTest {
        everySuspend { appService.create(any()) } throws IOException("Network error")
        val node = "test-node"

        val res =
            sut.createApplication(
                node = node,
                clientName = "app-name",
                website = "www.example.org",
                redirectUri = "app://",
                scopes = "read write",
            )

        assertNull(res)
        verifySuspend {
            provider.changeNode(node)
            appService.create(any())
        }
    }

    @Test
    fun `when createApplication then result is as expected`() = runTest {
        everySuspend { appService.create(any()) } returns
            Application(
                clientId = "client-id",
                clientSecret = "client-secret",
                name = "app-name",
                webSite = "www.example.org",
            )
        val node = "test-node"

        val res =
            sut.createApplication(
                node = node,
                clientName = "app-name",
                website = "www.example.org",
                redirectUri = "app://",
                scopes = "read write",
            )

        assertEquals(
            ClientApplicationModel(
                clientId = "client-id",
                clientSecret = "client-secret",
                name = "app-name",
                webSite = "www.example.org",
            ),
            res,
        )
        verifySuspend {
            provider.changeNode(node)
            appService.create(any())
        }
    }

    @Test
    fun `given valid credentials when validateApplicationCredentials then result is as expected`() = runTest {
        everySuspend { appService.verifyCredentials() } returns
            Application(
                clientId = "client-id",
                clientSecret = "client-secret",
                name = "app-name",
                webSite = "www.example.org",
            )
        val node = "test-node"
        val credentials = ApiCredentials.OAuth2("fake-access-token", "")

        val res =
            sut.validateApplicationCredentials(
                node = node,
                credentials = credentials,
            )

        assertTrue(res)
        verifySuspend {
            provider.changeNode(node)
            provider.setAuth(credentials = credentials.toServiceCredentials())
            appService.verifyCredentials()
        }
    }

    @Test
    fun `given invalid credentials when validateApplicationCredentials then result is as expected`() = runTest {
        everySuspend { appService.verifyCredentials() } throws IOException("Not found")
        val node = "test-node"
        val credentials = ApiCredentials.OAuth2("fake-access-token", "")

        val res =
            sut.validateApplicationCredentials(
                node = node,
                credentials = credentials,
            )

        assertFalse(res)
        verifySuspend {
            provider.changeNode(node)
            provider.setAuth(credentials = credentials.toServiceCredentials())
            appService.verifyCredentials()
        }
    }
}
