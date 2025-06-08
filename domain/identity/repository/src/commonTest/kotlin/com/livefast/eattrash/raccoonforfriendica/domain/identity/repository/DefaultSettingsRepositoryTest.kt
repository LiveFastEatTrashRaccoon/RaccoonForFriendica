package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.SettingsEntity
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultSettingsRepositoryTest {
    private val dao = mock<SettingsDao>(mode = MockMode.autoUnit)
    private val sut = DefaultSettingsRepository(settingsDao = dao)

    @Test
    fun `when changeCurrent then value is updated`() = runTest {
        val settings = SettingsModel(lang = "it")
        sut.changeCurrent(settings)

        val res = sut.current.value
        assertEquals(settings, res)
    }

    @Test
    fun `when get then result is as expected`() = runTest {
        val accountId = 1L
        everySuspend { dao.getBy(any()) } returns SettingsEntity(lang = "it")
        val settings = SettingsModel(lang = "it")

        val res = sut.get(accountId)

        assertEquals(settings, res)
        verifySuspend {
            dao.getBy(accountId)
        }
    }

    @Test
    fun `when create then result is as expected`() = runTest {
        val settings = SettingsModel(lang = "it", accountId = 1L)

        sut.create(settings)

        verifySuspend {
            dao.insert(SettingsEntity(lang = "it", accountId = 1L))
        }
    }

    @Test
    fun `when update then result is as expected`() = runTest {
        val settings = SettingsModel(lang = "it", accountId = 1L)

        sut.update(settings)

        verifySuspend {
            dao.update(SettingsEntity(lang = "it", accountId = 1L))
        }
    }

    @Test
    fun `when delete then result is as expected`() = runTest {
        val settings = SettingsModel(lang = "it", accountId = 1L)

        sut.delete(settings)

        verifySuspend {
            dao.delete(SettingsEntity(lang = "it", accountId = 1L))
        }
    }
}
