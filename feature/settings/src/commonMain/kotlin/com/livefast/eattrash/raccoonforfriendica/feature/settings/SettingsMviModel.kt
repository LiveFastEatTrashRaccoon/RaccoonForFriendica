package com.livefast.eattrash.raccoonforfriendica.feature.settings

import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.ThemeColor
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel

interface SettingsMviModel :
    ScreenModel,
    MviModel<SettingsMviModel.Intent, SettingsMviModel.State, SettingsMviModel.Effect> {
    sealed interface Intent {
        data class ChangeTheme(
            val theme: UiTheme,
        ) : Intent

        data class ChangeLanguage(
            val lang: String,
        ) : Intent

        data class ChangeFontFamily(
            val fontFamily: UiFontFamily,
        ) : Intent

        data class ChangeDynamicColors(
            val dynamicColors: Boolean,
        ) : Intent

        data class ChangeThemeColor(
            val themeColor: Color?,
        ) : Intent
    }

    data class State(
        val theme: UiTheme = UiTheme.Default,
        val lang: String? = null,
        val supportsDynamicColors: Boolean = false,
        val dynamicColors: Boolean = false,
        val fontFamily: UiFontFamily = UiFontFamily.Default,
        val themeColor: Color? = null,
        val availableThemeColors: List<ThemeColor> = emptyList(),
    )

    sealed interface Effect
}
