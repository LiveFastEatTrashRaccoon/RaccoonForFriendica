package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import app.cash.turbine.test
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.AccountEntity
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultAccountRepositoryTest {
    private val accountDao = mock<AccountDao>(mode = MockMode.autoUnit)

    private val sut =
        DefaultAccountRepository(accountDao = accountDao)

    @Test
    fun `given existing accounts when getAll then result is as expected`() =
        runTest {
            val entities =
                (0..<2).map {
                    AccountEntity(id = it.toLong(), handle = "account$it@example.org")
                }
            everySuspend { accountDao.getAll() } returns entities
            val accounts =
                (0..<2).map {
                    AccountModel(id = it.toLong(), handle = "account$it@example.org")
                }

            val res = sut.getAll()

            assertEquals(accounts, res)
            verifySuspend {
                accountDao.getAll()
            }
        }

    @Test
    fun `given no accounts when getAll then result is as expected`() =
        runTest {
            everySuspend { accountDao.getAll() } returns listOf()

            val res = sut.getAll()

            assertEquals(listOf(), res)
            verifySuspend {
                accountDao.getAll()
            }
        }

    @Test
    fun `given existing accounts when getAllAsFlow then result is as expected`() =
        runTest {
            val entities =
                flowOf(
                    (0..<2).map {
                        AccountEntity(id = it.toLong(), handle = "account$it@example.org")
                    },
                )
            everySuspend { accountDao.getAllAsFlow() } returns entities
            val accounts =
                (0..<2).map {
                    AccountModel(id = it.toLong(), handle = "account$it@example.org")
                }

            sut.getAllAsFlow().test {
                val res = awaitItem()
                assertEquals(accounts, res)
                cancelAndIgnoreRemainingEvents()
            }
            verify {
                accountDao.getAllAsFlow()
            }
        }

    @Test
    fun `given existing account when getBy ID then result is as expected`() =
        runTest {
            val id = 1L
            val handle = "account1@example.org"
            everySuspend { accountDao.getBy(id = any()) } returns
                AccountEntity(
                    id = id,
                    handle = handle,
                )
            val account = AccountModel(id = id, handle = handle)

            val res = sut.getBy(id)

            assertEquals(account, res)
            verifySuspend {
                accountDao.getBy(id)
            }
        }

    @Test
    fun `given existing account when getBy handle then result is as expected`() =
        runTest {
            val handle = "account1@example.org"
            everySuspend { accountDao.getBy(handle = any()) } returns
                AccountEntity(id = 1, handle = handle)
            val account = AccountModel(id = 1, handle = handle)

            val res = sut.getBy(handle)

            assertEquals(account, res)
            verifySuspend {
                accountDao.getBy(handle)
            }
        }

    @Test
    fun `given currently account when getActive then result is as expected`() =
        runTest {
            everySuspend { accountDao.getActive() } returns
                AccountEntity(id = 1, handle = "account1@example.org", active = true)
            val account =
                AccountModel(id = 1, handle = "account1@example.org", active = true)

            val res = sut.getActive()

            assertEquals(account, res)
            verifySuspend {
                accountDao.getActive()
            }
        }

    @Test
    fun `given no active account when getActive then result is as expected`() =
        runTest {
            everySuspend { accountDao.getActive() } returns null

            val res = sut.getActive()

            assertNull(res)
            verifySuspend {
                accountDao.getActive()
            }
        }

    @Test
    fun `given currently account when getActiveAsFlow then result is as expected`() =
        runTest {
            everySuspend { accountDao.getActiveAsFlow() } returns
                flowOf(
                    listOf(
                        AccountEntity(id = 1, handle = "account1@example.org", active = true),
                    ),
                )
            val account =
                AccountModel(id = 1, handle = "account1@example.org", active = true)

            sut.getActiveAsFlow().test {
                val res = awaitItem()
                assertEquals(account, res)
                cancelAndIgnoreRemainingEvents()
            }
            verifySuspend {
                accountDao.getActiveAsFlow()
            }
        }

    @Test
    fun `when create then interactions are as expected`() =
        runTest {
            val account =
                AccountModel(id = 1, handle = "account1@example.org")
            sut.create(account)

            verifySuspend {
                accountDao.insert(
                    AccountEntity(id = 1, handle = "account1@example.org"),
                )
            }
        }

    @Test
    fun `when delete then interactions are as expected`() =
        runTest {
            val account =
                AccountModel(id = 1, handle = "account1@example.org")
            sut.delete(account)

            verifySuspend {
                accountDao.delete(
                    AccountEntity(id = 1, handle = "account1@example.org"),
                )
            }
        }

    @Test
    fun `given currently account when setActive true then interactions is as expected`() =
        runTest {
            everySuspend { accountDao.getActive() } returns
                AccountEntity(id = 1, handle = "account1@example.org", active = true)
            val account =
                AccountModel(id = 2, handle = "account2@example.org", active = true)

            sut.setActive(account, true)

            verifySuspend {
                accountDao.getActive()
                accountDao.replaceActive(
                    old =
                        AccountEntity(id = 1, handle = "account1@example.org", active = false),
                    new =
                        AccountEntity(id = 2, handle = "account2@example.org", active = true),
                )
            }
        }

    @Test
    fun `given currently account when setActive false then interactions is as expected`() =
        runTest {
            everySuspend { accountDao.getActive() } returns
                AccountEntity(id = 1, handle = "account1@example.org", active = true)
            val account =
                AccountModel(id = 1, handle = "account1@example.org")

            sut.setActive(account, false)

            verifySuspend {
                accountDao.getActive()
                accountDao.update(
                    AccountEntity(id = 1, handle = "account1@example.org"),
                )
            }
        }

    @Test
    fun `given no active account when setActive true then interactions is as expected`() =
        runTest {
            everySuspend { accountDao.getActive() } returns null
            val account =
                AccountModel(id = 1, handle = "account1@example.org")

            sut.setActive(account, true)

            verifySuspend {
                accountDao.getActive()
                accountDao.update(
                    AccountEntity(id = 1, handle = "account1@example.org", active = true),
                )
            }
        }

    @Test
    fun `given no active account when setActive false then interactions is as expected`() =
        runTest {
            everySuspend { accountDao.getActive() } returns null
            val account =
                AccountModel(id = 1, handle = "account1@example.org")

            sut.setActive(account, false)

            verifySuspend {
                accountDao.getActive()
                accountDao.update(
                    AccountEntity(id = 1, handle = "account1@example.org"),
                )
            }
        }
}
