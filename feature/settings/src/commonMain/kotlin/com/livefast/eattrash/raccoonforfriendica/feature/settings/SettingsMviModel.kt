package com.livefast.eattrash.raccoonforfriendica.feature.settings

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.ThemeColor
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.appicon.AppIconVariant
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.ImageLoadingMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.NotificationMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.PushNotificationManagerState
import dev.icerock.moko.permissions.PermissionState
import kotlin.time.Duration

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

        data class ChangeExcludeRepliesFromTimeline(
            val value: Boolean,
        ) : Intent

        data class ChangeOpenGroupsInForumModeByDefault(
            val value: Boolean,
        ) : Intent

        data class ChangeMarkupMode(
            val mode: MarkupMode,
        ) : Intent

        data class ChangeMaxPostBodyLines(
            val value: Int,
        ) : Intent

        data class ChangeBackgroundNotificationCheckInterval(
            val duration: Duration?,
        ) : Intent

        data class ChangeAutoloadImages(
            val mode: ImageLoadingMode,
        ) : Intent

        data class ChangeNotificationMode(
            val mode: NotificationMode,
        ) : Intent

        data class SelectPushDistributor(
            val value: String,
        ) : Intent

        data object GrantPushNotificationsPermission : Intent

        data class ChangeCrashReportEnabled(
            val value: Boolean,
        ) : Intent

        data class ChangeHideNavigationBarWhileScrolling(
            val value: Boolean,
        ) : Intent

        data class ChangeAppIcon(
            val variant: AppIconVariant,
        ) : Intent

        data class ChangeBarTheme(
            val theme: UiBarTheme,
        ) : Intent

        data class ChangeTimelineLayout(
            val layout: TimelineLayout,
        ) : Intent

        data object ExportSettings : Intent

        data class ImportSettings(
            val content: String,
        ) : Intent

        data class ChangeReplyDepth(
            val depth: Int,
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
        val excludeRepliesFromTimeline: Boolean = false,
        val availableVisibilities: List<Visibility> = emptyList(),
        val openGroupsInForumModeByDefault: Boolean = true,
        val markupMode: MarkupMode = MarkupMode.HTML,
        val availableMarkupModes: List<MarkupMode> = emptyList(),
        val maxPostBodyLines: Int = Int.MAX_VALUE,
        val supportsNotifications: Boolean = false,
        val notificationMode: NotificationMode = NotificationMode.Disabled,
        val availableNotificationModes: List<NotificationMode> = emptyList(),
        val pullNotificationsRestricted: Boolean = false,
        val pushNotificationState: PushNotificationManagerState = PushNotificationManagerState.Unsupported,
        val backgroundNotificationCheckInterval: Duration? = null,
        val availablePushDistributors: List<String> = emptyList(),
        val imageLoadingMode: ImageLoadingMode = ImageLoadingMode.Always,
        val crashReportEnabled: Boolean = false,
        val crashReportRestartRequired: Boolean = false,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val appIconChangeSupported: Boolean = true,
        val appIconVariant: AppIconVariant = AppIconVariant.Default,
        val appIconRestartRequired: Boolean = false,
        val loading: Boolean = false,
        val supportSettingsImportExport: Boolean = true,
        val barTheme: UiBarTheme = UiBarTheme.Transparent,
        val timelineLayout: TimelineLayout = TimelineLayout.Full,
        val pushNotificationPermissionState: PermissionState = PermissionState.NotDetermined,
        val isBarThemeSupported: Boolean = false,
        val replyDepth: Int = 1,
    )

    sealed interface Effect {
        data class SaveSettings(
            val content: String,
        ) : Effect
    }
}
