package com.livefast.eattrash.raccoonforfriendica.feature.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeColorRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.l10n.L10nManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toInt
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toTimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toVisibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val l10nManager: L10nManager,
    private val themeRepository: ThemeRepository,
    private val colorSchemeProvider: ColorSchemeProvider,
    private val themeColorRepository: ThemeColorRepository,
    private val identityRepository: IdentityRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val circlesRepository: CirclesRepository,
    private val pullNotificationManager: PullNotificationManager,
) : DefaultMviModel<SettingsMviModel.Intent, SettingsMviModel.State, SettingsMviModel.Effect>(
        initialState = SettingsMviModel.State(),
    ),
    SettingsMviModel {
    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .onEach { currentUser ->
                    val circles = circlesRepository.getAll().orEmpty()
                    val isLogged = currentUser != null
                    val defaultTimelineTypes =
                        buildList {
                            this += TimelineType.All
                            if (isLogged) {
                                this += TimelineType.Subscriptions
                            }
                            this += TimelineType.Local
                        }
                    val timelineTypes =
                        defaultTimelineTypes + circles.map { TimelineType.Circle(circle = it) }
                    updateState {
                        it.copy(
                            availableTimelineTypes = timelineTypes,
                            isLogged = isLogged,
                            supportsBackgroundNotificationCheck = pullNotificationManager.isBackgroundCheckSupported,
                            isBackgroundNotificationCheckRestricted = pullNotificationManager.isBackgroundRestricted,
                        )
                    }
                }.launchIn(this)
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
            themeRepository.fontScale
                .onEach { fontScale ->
                    updateState {
                        it.copy(fontScale = fontScale)
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
                        val defaultCircle =
                            settings.defaultTimelineId?.let { circlesRepository.get(it) }
                        updateState {
                            it.copy(
                                dynamicColors = settings.dynamicColors,
                                defaultTimelineType =
                                    settings.defaultTimelineType
                                        .toTimelineType()
                                        .let { type ->

                                            when (type) {
                                                is TimelineType.Circle ->
                                                    type.copy(circle = defaultCircle)

                                                else -> type
                                            }
                                        },
                                includeNsfw = settings.includeNsfw,
                                blurNsfw = settings.blurNsfw,
                                urlOpeningMode = settings.urlOpeningMode,
                                defaultPostVisibility = settings.defaultPostVisibility.toVisibility(),
                                defaultReplyVisibility = settings.defaultReplyVisibility.toVisibility(),
                                excludeRepliesFromTimeline = settings.excludeRepliesFromTimeline,
                                openGroupsInForumModeByDefault = settings.openGroupsInForumModeByDefault,
                                markupMode = settings.markupMode,
                                maxPostBodyLines = settings.maxPostBodyLines,
                                backgroundNotificationCheckInterval = settings.pullNotificationCheckInterval,
                                autoloadImages = settings.autoloadImages,
                            )
                        }
                    }
                }.launchIn(this)
            supportedFeatureRepository.features
                .onEach { features ->
                    updateState {
                        it.copy(
                            availableVisibilities =
                                buildList {
                                    this += Visibility.Public
                                    this += Visibility.Unlisted
                                    this += Visibility.Private
                                    this += Visibility.Direct
                                },
                            availableMarkupModes =
                                buildList {
                                    this += MarkupMode.PlainText
                                    if (features.supportsBBCode) {
                                        this += MarkupMode.BBCode
                                    }
                                    this += MarkupMode.HTML
                                    if (features.supportsMarkdown) {
                                        this += MarkupMode.Markdown
                                    }
                                },
                        )
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

            is SettingsMviModel.Intent.ChangeFontScale ->
                screenModelScope.launch {
                    changeFontScale(intent.scale)
                }

            is SettingsMviModel.Intent.ChangeThemeColor ->
                screenModelScope.launch {
                    changeThemeColor(intent.themeColor)
                }
            is SettingsMviModel.Intent.ChangeDefaultTimelineType ->
                screenModelScope.launch {
                    changeDefaultTimelineType(intent.type)
                }

            is SettingsMviModel.Intent.ChangeIncludeNsfw ->
                screenModelScope.launch {
                    changeIncludeNsfw(intent.value)
                }

            is SettingsMviModel.Intent.ChangeBlurNsfw ->
                screenModelScope.launch {
                    changeBlurNsfw(intent.value)
                }

            is SettingsMviModel.Intent.ChangeUrlOpeningMode ->
                screenModelScope.launch {
                    changeUrlOpeningMode(intent.mode)
                }

            is SettingsMviModel.Intent.ChangeDefaultPostVisibility ->
                screenModelScope.launch {
                    changeDefaultPostVisibility(intent.visibility)
                }

            is SettingsMviModel.Intent.ChangeDefaultReplyVisibility ->
                screenModelScope.launch {
                    changeDefaultReplyVisibility(intent.visibility)
                }

            is SettingsMviModel.Intent.ChangeExcludeRepliesFromTimeline ->
                screenModelScope.launch {
                    changeExcludeRepliesFromTimeline(intent.value)
                }

            is SettingsMviModel.Intent.ChangeOpenGroupsInForumModeByDefault ->
                screenModelScope.launch {
                    changeOpenGroupsInForumModeByDefault(intent.value)
                }

            is SettingsMviModel.Intent.ChangeMarkupMode ->
                screenModelScope.launch {
                    changeMarkupMode(intent.mode)
                }

            is SettingsMviModel.Intent.ChangeMaxPostBodyLines ->
                screenModelScope.launch {
                    changeMaxPostBodyLines(intent.value)
                }

            is SettingsMviModel.Intent.ChangeBackgroundNotificationCheckInterval ->
                screenModelScope.launch {
                    changePullNotificationCheckInterval(intent.duration)
                }

            is SettingsMviModel.Intent.ChangeAutoloadImages ->
                screenModelScope.launch {
                    changeAutoloadImages(intent.value)
                }
        }
    }

    private suspend fun changeLanguage(value: String) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(lang = value)
        saveSettings(newSettings)
    }

    private suspend fun changeTheme(value: UiTheme) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(theme = value)
        saveSettings(newSettings)
    }

    private suspend fun changeFontFamily(value: UiFontFamily) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(fontFamily = value)
        saveSettings(newSettings)
    }

    private suspend fun changeFontScale(value: UiFontScale) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(fontScale = value)
        saveSettings(newSettings)
    }

    private suspend fun changeDynamicColors(value: Boolean) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(dynamicColors = value)
        saveSettings(newSettings)
    }

    private suspend fun changeThemeColor(value: Color?) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(customSeedColor = value?.toArgb())
        saveSettings(newSettings)
    }

    private suspend fun changeDefaultTimelineType(type: TimelineType) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings =
            currentSettings.copy(
                defaultTimelineType = type.toInt(),
                defaultTimelineId = (type as? TimelineType.Circle)?.circle?.id,
            )
        saveSettings(newSettings)
    }

    private suspend fun changeDefaultPostVisibility(visibility: Visibility) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(defaultPostVisibility = visibility.toInt())
        saveSettings(newSettings)
    }

    private suspend fun changeDefaultReplyVisibility(visibility: Visibility) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(defaultReplyVisibility = visibility.toInt())
        saveSettings(newSettings)
    }

    private suspend fun changeIncludeNsfw(value: Boolean) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(includeNsfw = value)
        saveSettings(newSettings)
    }

    private suspend fun changeBlurNsfw(value: Boolean) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(blurNsfw = value)
        saveSettings(newSettings)
    }

    private suspend fun changeUrlOpeningMode(value: UrlOpeningMode) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(urlOpeningMode = value)
        saveSettings(newSettings)
    }

    private suspend fun changeExcludeRepliesFromTimeline(value: Boolean) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(excludeRepliesFromTimeline = value)
        saveSettings(newSettings)
    }

    private suspend fun changeOpenGroupsInForumModeByDefault(value: Boolean) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(openGroupsInForumModeByDefault = value)
        saveSettings(newSettings)
    }

    private suspend fun changeMarkupMode(value: MarkupMode) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(markupMode = value)
        saveSettings(newSettings)
    }

    private suspend fun changeMaxPostBodyLines(value: Int) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(maxPostBodyLines = value)
        saveSettings(newSettings)
    }

    private suspend fun changePullNotificationCheckInterval(value: Duration?) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(pullNotificationCheckInterval = value)
        saveSettings(newSettings)
    }

    private suspend fun changeAutoloadImages(value: Boolean) {
        val currentSettings = settingsRepository.current.value ?: return
        val newSettings = currentSettings.copy(autoloadImages = value)
        saveSettings(newSettings)
    }

    private suspend fun saveSettings(newSettings: SettingsModel) {
        settingsRepository.update(newSettings)
        settingsRepository.changeCurrent(newSettings)
    }
}
