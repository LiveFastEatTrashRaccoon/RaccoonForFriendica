package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultExportSettingsUseCaseTest {
    private val settingsRepository = mock<SettingsRepository>(MockMode.autoUnit)
    private val sut = DefaultExportSettingsUseCase(settingsRepository)

    @Test
    fun `when invoked then interactions are as expected`() = runTest {
        val settings =
            SettingsModel(
                theme = UiTheme.Dark,
            )
        every { settingsRepository.current } returns MutableStateFlow(settings)
        val expected = jsonSerializationStrategy.encodeToString(settings.toData())

        val res = sut()
        assertEquals(expected, res)

        verify {
            settingsRepository.current
        }
    }

    @Test
    fun `given no settings when invoked then interactions are as expected`() = runTest {
        every { settingsRepository.current } returns MutableStateFlow(null)

        val res = sut()
        assertEquals("", res)

        verify {
            settingsRepository.current
        }
    }
}
