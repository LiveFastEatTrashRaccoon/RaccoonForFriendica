package com.livefast.eattrash.raccoonforfriendica.feature.settings

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.ThemeColor
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode

@Stable
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

        data class ChangeFontScale(
            val scale: UiFontScale,
        ) : Intent

        data class ChangeDynamicColors(
            val dynamicColors: Boolean,
        ) : Intent

        data class ChangeThemeColor(
            val themeColor: Color?,
        ) : Intent

        data class ChangeDefaultTimelineType(
            val type: TimelineType,
        ) : Intent

        data class ChangeIncludeNsfw(
            val value: Boolean,
        ) : Intent

        data class ChangeBlurNsfw(
            val value: Boolean,
        ) : Intent

        data class ChangeUrlOpeningMode(
            val mode: UrlOpeningMode,
        ) : Intent

        data class ChangeDefaultPostVisibility(
            val visibility: Visibility,
        ) : Intent

        data class ChangeDefaultReplyVisibility(
            val visibility: Visibility,
        ) : Intent
    }

    data class State(
        val isLogged: Boolean = false,
        val theme: UiTheme = UiTheme.Default,
        val lang: String? = null,
        val supportsDynamicColors: Boolean = false,
        val dynamicColors: Boolean = false,
        val fontFamily: UiFontFamily = UiFontFamily.Default,
        val fontScale: UiFontScale = UiFontScale.Normal,
        val themeColor: Color? = null,
        val availableThemeColors: List<ThemeColor> = emptyList(),
        val availableTimelineTypes: List<TimelineType> = emptyList(),
        val defaultTimelineType: TimelineType = TimelineType.Local,
        val includeNsfw: Boolean = true,
        val blurNsfw: Boolean = true,
        val urlOpeningMode: UrlOpeningMode = UrlOpeningMode.External,
        val defaultPostVisibility: Visibility = Visibility.Public,
        val defaultReplyVisibility: Visibility = Visibility.Public,
    )

    sealed interface Effect
}
