package com.livefast.eattrash.raccoonforfriendica.feature.settings

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel

interface SettingsMviModel :
    ScreenModel,
    MviModel<SettingsMviModel.Intent, SettingsMviModel.State, SettingsMviModel.Effect> {
    sealed interface Intent {
        data class ChangeTheme(
            val theme: UiTheme?,
        ) : Intent

        data class ChangeLanguage(
            val lang: String,
        ) : Intent
    }

    data class State(
        val theme: UiTheme? = null,
        val lang: String? = null,
    )

    sealed interface Effect
}
