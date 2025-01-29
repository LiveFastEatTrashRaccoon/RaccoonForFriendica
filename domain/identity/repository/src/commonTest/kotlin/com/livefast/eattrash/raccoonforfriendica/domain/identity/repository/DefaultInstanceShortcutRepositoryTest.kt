package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultInstanceShortcutRepositoryTest {
    private val keyStore = mock<TemporaryKeyStore>(MockMode.autoUnit)
    private val sut =
        DefaultInstanceShortcutRepository(
            keyStore = keyStore,
        )

    @Test
    fun givenNoData_whenGetAll_thenResultAndInteractionsIsAsExpected() =
        runTest {
            val accountId = 1L
            every { keyStore.get(any(), any<List<String>>()) } returns emptyList()

            val res = sut.getAll(accountId)

            assertEquals(emptyList(), res)
            verify {
                keyStore.get("$KEY_PREFIX.$accountId.items", emptyList())
            }
        }

    @Test
    fun givenData_whenGetAll_thenResultAndInteractionsIsAsExpected() =
        runTest {
            val accountId = 1L
            val fakeList = listOf("node")
            every { keyStore.get(any(), any<List<String>>()) } returns fakeList

            val res = sut.getAll(accountId)

            assertEquals(fakeList, res)
            verify {
                keyStore.get("$KEY_PREFIX.$accountId.items", emptyList())
            }
        }

    @Test
    fun givenDataForOtherUser_whenGetAll_thenResultAndInteractionsIsAsExpected() =
        runTest {
            val otherAccountId = 1L
            val accountId = 2L
            val fakeList = listOf("node")
            every {
                keyStore.get(
                    "$KEY_PREFIX.$otherAccountId.items",
                    any<List<String>>(),
                )
            } returns fakeList
            every {
                keyStore.get(
                    "$KEY_PREFIX.$accountId.items",
                    any<List<String>>(),
                )
            } returns emptyList()

            val res = sut.getAll(accountId)

            assertEquals(emptyList(), res)
            verify {
                keyStore.get("$KEY_PREFIX.$accountId.items", emptyList())
            }
        }

    @Test
    fun whenCreate_thenInteractionsAreAsExpected() =
        runTest {
            val accountId = 1L
            val node = "node"
            every {
                keyStore.get(
                    "$KEY_PREFIX.$accountId.items",
                    any<List<String>>(),
                )
            } returns emptyList()

            sut.create(accountId = accountId, node = node)

            verify {
                keyStore.save("$KEY_PREFIX.$accountId.items", listOf(node))
            }
        }

    @Test
    fun givenAlreadyThere_whenCreate_thenInteractionsAreAsExpected() =
        runTest {
            val accountId = 1L
            val node = "node"
            val fakeList = listOf(node)
            every {
                keyStore.get(
                    "$KEY_PREFIX.$accountId.items",
                    any<List<String>>(),
                )
            } returns fakeList

            sut.create(accountId = accountId, node = node)

            verify {
                keyStore.save("$KEY_PREFIX.$accountId.items", fakeList)
            }
        }

    @Test
    fun whenDelete_thenInteractionsAreAsExpected() =
        runTest {
            val accountId = 1L
            val node = "node"
            val fakeList = listOf(node)
            every {
                keyStore.get(
                    "$KEY_PREFIX.$accountId.items",
                    any<List<String>>(),
                )
            } returns fakeList

            sut.delete(accountId = accountId, node = node)

            verify {
                keyStore.save("$KEY_PREFIX.$accountId.items", emptyList())
            }
        }

    @Test
    fun givenNotAlreadyThere_whenDelete_thenInteractionsAreAsExpected() =
        runTest {
            val accountId = 1L
            val node = "node"
            val fakeList = listOf("other")
            every {
                keyStore.get(
                    "$KEY_PREFIX.$accountId.items",
                    any<List<String>>(),
                )
            } returns fakeList

            sut.delete(accountId = accountId, node = node)

            verify {
                keyStore.save("$KEY_PREFIX.$accountId.items", fakeList)
            }
        }

    companion object {
        private const val KEY_PREFIX = "InstanceShortcutRepository"
    }
}
