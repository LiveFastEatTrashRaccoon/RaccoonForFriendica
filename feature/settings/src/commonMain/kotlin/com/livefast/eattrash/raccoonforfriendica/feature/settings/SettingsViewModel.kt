package com.livefast.eattrash.raccoonforfriendica.feature.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeColorRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.l10n.L10nManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val l10nManager: L10nManager,
    private val themeRepository: ThemeRepository,
    private val colorSchemeProvider: ColorSchemeProvider,
    private val themeColorRepository: ThemeColorRepository,
) : DefaultMviModel<SettingsMviModel.Intent, SettingsMviModel.State, SettingsMviModel.Effect>(
        initialState = SettingsMviModel.State(),
    ),
    SettingsMviModel {
    init {
        screenModelScope.launch {
            l10nManager.lyricist.state
                .onEach { state ->
                    updateState {
                        it.copy(lang = state.languageTag)
                    }
                }.launchIn(this)
            themeRepository.theme
                .onEach { theme ->
                    updateState {
                        it.copy(theme = theme)
                    }
                }.launchIn(this)
            themeRepository.fontFamily
                .onEach { fontFamily ->
                    updateState {
                        it.copy(fontFamily = fontFamily)
                    }
                }.launchIn(this)
            themeRepository.customSeedColor
                .onEach { seedColor ->
                    updateState {
                        it.copy(themeColor = seedColor)
                    }
                }.launchIn(this)
            settingsRepository.current
                .onEach { settings ->
                    if (settings != null) {
                        updateState {
                            it.copy(
                                dynamicColors = settings.dynamicColors,
                            )
                        }
                    }
                }.launchIn(this)

            val supportsDynamicColors = colorSchemeProvider.supportsDynamicColors
            updateState {
                it.copy(
                    supportsDynamicColors = supportsDynamicColors,
                    availableThemeColors = themeColorRepository.getColors(),
                )
            }
        }
    }

    override fun reduce(intent: SettingsMviModel.Intent) {
        when (intent) {
            is SettingsMviModel.Intent.ChangeLanguage ->
                screenModelScope.launch {
                    changeLanguage(intent.lang)
                }

            is SettingsMviModel.Intent.ChangeTheme ->
                screenModelScope.launch {
                    changeTheme(intent.theme)
                }

            is SettingsMviModel.Intent.ChangeDynamicColors ->
                screenModelScope.launch {
                    changeDynamicColors(intent.dynamicColors)
                }

            is SettingsMviModel.Intent.ChangeFontFamily ->
                screenModelScope.launch {
                    changeFontFamily(intent.fontFamily)
                }

            is SettingsMviModel.Intent.ChangeThemeColor ->
                screenModelScope.launch {
                    changeThemeColor(intent.themeColor)
                }
        }
    }

    private suspend fun changeLanguage(value: String) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(lang = value)
        settingsRepository.update(newSettings)
        settingsRepository.changeCurrent(newSettings)
    }

    private suspend fun changeTheme(value: UiTheme) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(theme = value)
        settingsRepository.update(newSettings)
        settingsRepository.changeCurrent(newSettings)
    }

    private suspend fun changeFontFamily(value: UiFontFamily) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(fontFamily = value)
        settingsRepository.update(newSettings)
        settingsRepository.changeCurrent(newSettings)
    }

    private suspend fun changeDynamicColors(value: Boolean) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(dynamicColors = value)
        settingsRepository.update(newSettings)
        settingsRepository.changeCurrent(newSettings)
    }

    private suspend fun changeThemeColor(value: Color?) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(customSeedColor = value?.toArgb())
        settingsRepository.update(newSettings)
        settingsRepository.changeCurrent(newSettings)
    }
}
