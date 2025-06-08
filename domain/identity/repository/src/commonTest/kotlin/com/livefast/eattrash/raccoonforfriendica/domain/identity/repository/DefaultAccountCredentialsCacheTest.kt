package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultAccountCredentialsCacheTest {
    private val keyStore =
        mock<TemporaryKeyStore>(MockMode.autoUnit) {
            every { get(any<String>(), any<String>()) } returns ""
        }

    private val sut = DefaultAccountCredentialsCache(keyStore)

    @Test
    fun `given no credentials stored when get then result is as expected`() {
        val accountId = 1L

        val res = sut.get(accountId)

        assertNull(res)
    }

    @Test
    fun `given OAuth credentials stored when get then result is as expected`() {
        val accountId = 1L
        every { keyStore[getKey(accountId, "type"), any<String>()] } returns "OAuth2"
        every { keyStore[getKey(accountId, "part1"), any<String>()] } returns "fake-access-token"
        every { keyStore[getKey(accountId, "part2"), any<String>()] } returns ""

        val res = sut.get(accountId)

        assertEquals(
            ApiCredentials.OAuth2(accessToken = "fake-access-token", refreshToken = ""),
            res,
        )
    }

    @Test
    fun `given basic credentials stored when get then result is as expected`() {
        val accountId = 1L
        every { keyStore[getKey(accountId, "type"), any<String>()] } returns "HTTPBasic"
        every { keyStore[getKey(accountId, "part1"), any<String>()] } returns "fake1"
        every { keyStore[getKey(accountId, "part2"), any<String>()] } returns "fake2"

        val res = sut.get(accountId)

        assertEquals(ApiCredentials.HttpBasic(user = "fake1", pass = "fake2"), res)
    }

    @Test
    fun `when save OAuth credentials then interactions are expected`() {
        val accountId = 1L

        sut.save(
            accountId,
            ApiCredentials.OAuth2(accessToken = "fake-access-token", refreshToken = ""),
        )

        verify {
            keyStore.save(getKey(accountId, "type"), "OAuth2")
            keyStore.save(getKey(accountId, "part1"), "fake-access-token")
            keyStore.save(getKey(accountId, "part2"), "")
        }
    }

    @Test
    fun `when save basic credentials then interactions are expected`() {
        val accountId = 1L

        sut.save(
            accountId,
            ApiCredentials.HttpBasic(user = "fake1", pass = "fake2"),
        )

        verify {
            keyStore.save(getKey(accountId, "type"), "HTTPBasic")
            keyStore.save(getKey(accountId, "part1"), "fake1")
            keyStore.save(getKey(accountId, "part2"), "fake2")
        }
    }

    @Test
    fun `when remove then interactions are expected`() {
        val accountId = 1L

        sut.remove(accountId)

        verify {
            keyStore.remove(getKey(accountId, "type"))
            keyStore.remove(getKey(accountId, "part1"))
            keyStore.remove(getKey(accountId, "part2"))
        }
    }

    private fun getKey(accountId: Long, key: String) = "AccountCredentialsRepository.$accountId.$key"
}
