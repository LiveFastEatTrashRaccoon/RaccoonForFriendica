package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlin.test.Test

class DefaultImportSettingsUseCaseTest {
    private val settingsRepository = mock<SettingsRepository>(MockMode.autoUnit)
    private val sut = DefaultImportSettingsUseCase(settingsRepository)

    @Test
    fun `when invoked then interactions are as expected`() =
        runTest {
            val settings =
                SettingsModel(
                    theme = UiTheme.Dark,
                )
            val currentSettings = SettingsModel()
            every { settingsRepository.current } returns MutableStateFlow(currentSettings)

            val serialized = jsonSerializationStrategy.encodeToString(settings.toData())
            sut(serialized)

            verifySuspend {
                settingsRepository.update(settings)
                settingsRepository.changeCurrent(settings)
            }
        }

    @Test
    fun `given no settings when invoked then interactions are as expected`() =
        runTest {
            val settings =
                SettingsModel(
                    theme = UiTheme.Dark,
                )
            every { settingsRepository.current } returns MutableStateFlow(null)

            val serialized = jsonSerializationStrategy.encodeToString(settings.toData())
            sut(serialized)

            verifySuspend(VerifyMode.not) {
                settingsRepository.update(settings)
                settingsRepository.changeCurrent(settings)
            }
        }
}
