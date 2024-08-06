package com.livefast.eattrash.raccoonforfriendica.feature.settings

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.l10n.L10nManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val l10nManager: L10nManager,
    private val themeRepository: ThemeRepository,
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
            themeRepository.uiTheme
                .onEach { theme ->
                    updateState {
                        it.copy(theme = theme)
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: SettingsMviModel.Intent) {
        when (intent) {
            is SettingsMviModel.Intent.ChangeLanguage -> {
                l10nManager.changeLanguage(intent.lang)
            }

            is SettingsMviModel.Intent.ChangeTheme -> {
                themeRepository.changeUiTheme(intent.theme)
            }
        }
    }
}
