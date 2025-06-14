package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultStopWordRepositoryTest {
    private val keyStore = mock<TemporaryKeyStore>(MockMode.autoUnit)
    private val sut =
        DefaultStopWordRepository(
            keyStore = keyStore,
        )

    @Test
    fun givenNoData_whenGetForAnonymousUser_thenResultAndInteractionsIsAsExpected() = runTest {
        everySuspend { keyStore.get(any(), any<List<String>>()) } returns emptyList()

        val res = sut.get(null)

        assertEquals(emptyList(), res)
        verifySuspend {
            keyStore.get("$KEY_PREFIX.items", emptyList())
        }
    }

    @Test
    fun givenData_whenGetForAnonymousUser_thenResultAndInteractionsIsAsExpected() = runTest {
        val fakeList = listOf("word")
        everySuspend { keyStore.get(any(), any<List<String>>()) } returns fakeList

        val res = sut.get(null)

        assertEquals(fakeList, res)
        verifySuspend {
            keyStore.get("$KEY_PREFIX.items", emptyList())
        }
    }

    @Test
    fun givenNoData_whenGetForLoggedUser_thenResultAndInteractionsIsAsExpected() = runTest {
        val accountId = 1L
        everySuspend { keyStore.get(any(), any<List<String>>()) } returns emptyList()

        val res = sut.get(accountId)

        assertEquals(emptyList(), res)
        verifySuspend {
            keyStore.get("$KEY_PREFIX.$accountId.items", emptyList())
        }
    }

    @Test
    fun givenData_whenGetForLoggedUser_thenResultAndInteractionsIsAsExpected() = runTest {
        val accountId = 1L
        val fakeList = listOf("word")
        everySuspend { keyStore.get(any(), any<List<String>>()) } returns fakeList

        val res = sut.get(accountId)

        assertEquals(fakeList, res)
        verifySuspend {
            keyStore.get("$KEY_PREFIX.$accountId.items", emptyList())
        }
    }

    @Test
    fun givenDataForOtherUser_whenGetForLoggedAccount_thenResultAndInteractionsIsAsExpected() = runTest {
        val otherAccountId = 1L
        val accountId = 2L
        val fakeList = listOf("word")
        everySuspend {
            keyStore.get(
                "$KEY_PREFIX.$otherAccountId.items",
                any<List<String>>(),
            )
        } returns fakeList
        everySuspend {
            keyStore.get(
                "$KEY_PREFIX.$accountId.items",
                any<List<String>>(),
            )
        } returns emptyList()

        val res = sut.get(accountId)

        assertEquals(emptyList(), res)
        verifySuspend {
            keyStore.get("$KEY_PREFIX.$accountId.items", emptyList())
        }
    }

    @Test
    fun givenDataForOtherUser_whenGetForAnonymousAccount_thenResultAndInteractionsIsAsExpected() = runTest {
        val otherAccountId = 1
        val fakeList = listOf("word")
        everySuspend {
            keyStore.get(
                "$KEY_PREFIX.$otherAccountId.items",
                any<List<String>>(),
            )
        } returns fakeList
        everySuspend {
            keyStore.get(
                "$KEY_PREFIX.items",
                any<List<String>>(),
            )
        } returns emptyList()

        val res = sut.get(null)

        assertEquals(emptyList(), res)
        verifySuspend {
            keyStore.get("$KEY_PREFIX.items", emptyList())
        }
    }

    @Test
    fun whenUpdateAnonymousUser_thenInteractionsAreAsExpected() = runTest {
        val fakeList = listOf("word")
        sut.update(accountId = null, items = fakeList)

        verifySuspend {
            keyStore.save("$KEY_PREFIX.items", fakeList)
        }
    }

    @Test
    fun whenUpdateLoggedUser_thenInteractionsAreAsExpected() = runTest {
        val accountId = 1L
        val fakeList = listOf("word")

        sut.update(accountId = accountId, items = fakeList)

        verifySuspend {
            keyStore.save("$KEY_PREFIX.$accountId.items", fakeList)
        }
    }

    companion object {
        private const val KEY_PREFIX = "StopWordRepository"
    }
}
