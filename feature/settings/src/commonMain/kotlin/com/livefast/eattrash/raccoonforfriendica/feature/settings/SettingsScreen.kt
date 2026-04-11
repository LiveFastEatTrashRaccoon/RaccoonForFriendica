package com.livefast.eattrash.raccoonforfriendica.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.CommentBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toColor
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toEmoji
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toScaleFactor
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.rememberThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toTypography
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomColorPickerDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.EditTwoTextualInfosDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.MultiColorPreview
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AboutDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsColorRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsMultiColorRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsSwitchRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Locales
import com.livefast.eattrash.raccoonforfriendica.core.l10n.toLanguageFlag
import com.livefast.eattrash.raccoonforfriendica.core.l10n.toLanguageName
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.rememberMainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.rememberNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import com.livefast.eattrash.raccoonforfriendica.core.utils.appicon.AppIconVariant
import com.livefast.eattrash.raccoonforfriendica.core.utils.appicon.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.optimizedForLargeScreens
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getPrettyDuration
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.rememberFileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.permissions.PermissionControllerWrapper
import com.livefast.eattrash.raccoonforfriendica.core.utils.permissions.PermissionControllerWrapperBindEffect
import com.livefast.eattrash.raccoonforfriendica.core.utils.permissions.PermissionState
import com.livefast.eattrash.raccoonforfriendica.core.utils.permissions.PermissionsControllerWrapperFactory
import com.livefast.eattrash.raccoonforfriendica.core.utils.permissions.rememberPermissionsControllerWrapperFactory
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.ImageLoadingMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.NotificationMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.PushNotificationManagerState
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.toReadableName
import com.livefast.eattrash.raccoonforfriendica.feature.settings.di.SettingsViewModelArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val factory: PermissionsControllerWrapperFactory = rememberPermissionsControllerWrapperFactory()
    val controller: PermissionControllerWrapper =
        remember(factory) {
            factory.create()
        }
    val model: SettingsMviModel = getViewModel<SettingsViewModel>(arg = SettingsViewModelArgs(controller))
    val uiState by model.uiState.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val navigationCoordinator = rememberNavigationCoordinator()
    val mainRouter = rememberMainRouter()
    val scope = rememberCoroutineScope()
    val fileSystemManager = rememberFileSystemManager()
    val themeRepository = rememberThemeRepository()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = LocalStrings.current.messageSuccess
    val errorMessage = LocalStrings.current.messageGenericError
    var languageBottomSheetOpened by remember { mutableStateOf(false) }
    var themeBottomSheetOpened by remember { mutableStateOf(false) }
    var commentBarThemeBottomSheetOpened by remember { mutableStateOf(false) }
    var fontFamilyBottomSheetOpened by remember { mutableStateOf(false) }
    var fontScaleBottomSheetOpened by remember { mutableStateOf(false) }
    var themeColorBottomSheetOpened by remember { mutableStateOf(false) }
    var defaultTimelineTypeBottomSheetOpened by remember { mutableStateOf(false) }
    var urlOpeningModeBottomSheetOpened by remember { mutableStateOf(false) }
    var customColorPickerDialogOpened by remember { mutableStateOf(false) }
    var defaultPostVisibilityBottomSheetOpened by remember { mutableStateOf(false) }
    var defaultReplyVisibilityBottomSheetOpened by remember { mutableStateOf(false) }
    var markupModeBottomSheetOpened by remember { mutableStateOf(false) }
    var maxPostBodyLinesBottomSheetOpened by remember { mutableStateOf(false) }
    var backgroundNotificationCheckIntervalDialogOpened by remember { mutableStateOf(false) }
    var notificationModeBottomSheetOpened by remember { mutableStateOf(false) }
    var pushNotificationDistributorBottomSheetOpened by remember { mutableStateOf(false) }
    var imageLoadingModeBottomSheetOpened by remember { mutableStateOf(false) }
    var appIconBottomSheetOpened by remember { mutableStateOf(false) }
    var barThemeBottomSheetOpened by remember { mutableStateOf(false) }
    var fileInputOpened by remember { mutableStateOf(false) }
    var settingsContent by remember { mutableStateOf<String?>(null) }
    var timelineLayoutBottomSheetOpened by remember { mutableStateOf(false) }
    var replyDepthBottomSheepOpened by remember { mutableStateOf(false) }
    var aboutDialogOpened by remember { mutableStateOf(false) }
    var manageTranslationProvidersOpened by remember { mutableStateOf(false) }
    var translationProviderConfigToDelete by remember { mutableStateOf<TranslationProviderConfig?>(null) }
    var addTranslationProviderConfigDialogOpened by remember { mutableStateOf(false) }

    PermissionControllerWrapperBindEffect(controller = controller)
    LaunchedEffect(model) {
        model.effects
            .onEach { evt ->
                when (evt) {
                    is SettingsMviModel.Effect.SaveSettings -> {
                        settingsContent = evt.content
                    }
                }
            }.launchIn(this)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                windowInsets = topAppBarState.toWindowInsets().optimizedForLargeScreens(),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = LocalStrings.current.settingsTitle,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    if (navigationCoordinator.canPop.value && isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)) {
                        IconButton(
                            onClick = {
                                navigationCoordinator.pop()
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.arrowBack,
                                contentDescription = LocalStrings.current.actionGoBack,
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    snackbarData = data,
                )
            }
        },
        content = { padding ->
            Box(
                modifier =
                Modifier
                    .padding(padding)
                    .then(
                        if (uiState.hideNavigationBarWhileScrolling) {
                            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                        } else {
                            Modifier
                        },
                    ),
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                ) {
                    // General section
                    SettingsHeader(
                        title = LocalStrings.current.settingsHeaderGeneral,
                        icon = LocalResources.current.settingsFill,
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsItemLanguage,
                        value =
                        buildString {
                            append(uiState.lang.toLanguageName().orEmpty())
                            append("  ")
                            append(uiState.lang.toLanguageFlag().orEmpty())
                        },
                        onTap = {
                            languageBottomSheetOpened = true
                        },
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsItemDefaultTimelineType,
                        value = uiState.defaultTimelineType.toReadableName(),
                        onTap = {
                            defaultTimelineTypeBottomSheetOpened = true
                        },
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsItemDefaultPostVisibility,
                        value = uiState.defaultPostVisibility.toReadableName(),
                        onTap = {
                            defaultPostVisibilityBottomSheetOpened = true
                        },
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsItemDefaultReplyVisibility,
                        value = uiState.defaultReplyVisibility.toReadableName(),
                        onTap = {
                            defaultReplyVisibilityBottomSheetOpened = true
                        },
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsItemUrlOpeningMode,
                        value = uiState.urlOpeningMode.toReadableName(),
                        onTap = {
                            urlOpeningModeBottomSheetOpened = true
                        },
                    )
                    SettingsSwitchRow(
                        title = LocalStrings.current.settingsItemExcludeRepliesFromTimeline,
                        value = uiState.excludeRepliesFromTimeline,
                        onValueChange = {
                            model.reduce(
                                SettingsMviModel.Intent.ChangeExcludeRepliesFromTimeline(it),
                            )
                        },
                    )
                    SettingsSwitchRow(
                        title = LocalStrings.current.settingsItemOpenGroupsInForumModeByDefault,
                        value = uiState.openGroupsInForumModeByDefault,
                        onValueChange = {
                            model.reduce(
                                SettingsMviModel.Intent.ChangeOpenGroupsInForumModeByDefault(it),
                            )
                        },
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsAutoloadImages,
                        value = uiState.imageLoadingMode.toReadableName(),
                        onTap = {
                            imageLoadingModeBottomSheetOpened = true
                        },
                    )

                    if (uiState.isLogged) {
                        SettingsRow(
                            title = LocalStrings.current.settingsItemMarkupMode,
                            value = uiState.markupMode.toReadableName(),
                            onTap = {
                                markupModeBottomSheetOpened = true
                            },
                        )
                    }

                    SettingsRow(
                        title = LocalStrings.current.settingsItemMaxPostBodyLines,
                        value = uiState.maxPostBodyLines.toMaxBodyLinesReadableName(),
                        onTap = {
                            maxPostBodyLinesBottomSheetOpened = true
                        },
                    )

                    if (uiState.isLogged && uiState.supportsNotifications) {
                        SettingsRow(
                            title = LocalStrings.current.settingsItemNotificationMode,
                            value = uiState.notificationMode.toReadableName(),
                            onTap = {
                                notificationModeBottomSheetOpened = true
                            },
                        )
                        if (uiState.notificationMode == NotificationMode.Pull) {
                            SettingsRow(
                                title = LocalStrings.current.settingsOptionBackgroundNotificationCheck,
                                subtitle =
                                if (uiState.pullNotificationsRestricted) {
                                    LocalStrings.current.settingsSubtitleBackgroundNotificationRestricted
                                } else {
                                    LocalStrings.current.settingsSubtitleBackgroundNotificationNotRestricted
                                },
                                value =
                                uiState.backgroundNotificationCheckInterval?.getPrettyDuration(
                                    secondsLabel = LocalStrings.current.timeSecondShort,
                                    minutesLabel = LocalStrings.current.timeMinuteShort,
                                    hoursLabel = LocalStrings.current.timeHourShort,
                                    daysLabel = LocalStrings.current.dateDayShort,
                                    finePrecision = false,
                                ) ?: LocalStrings.current.durationNever,
                                onTap = {
                                    backgroundNotificationCheckIntervalDialogOpened = true
                                },
                            )
                        }
                        if (uiState.notificationMode == NotificationMode.Push) {
                            val permissionState = uiState.pushNotificationPermissionState
                            when (permissionState) {
                                PermissionState.NotGranted,
                                PermissionState.Denied,
                                ->
                                    SettingsRow(
                                        title = LocalStrings.current.settingsPushNotificationPermissionNotGranted,
                                        value = LocalStrings.current.actionGrantPermission,
                                        onTap = {
                                            model.reduce(SettingsMviModel.Intent.GrantPushNotificationsPermission)
                                        },
                                    )

                                PermissionState.DeniedAlways ->
                                    SettingsRow(
                                        title = LocalStrings.current
                                            .settingsPushNotificationPermissionDeniedPermanently,
                                        value = LocalStrings.current.actionOpenSettings,
                                        onTap = {
                                        },
                                    )

                                else -> Unit
                            }
                            SettingsRow(
                                title = LocalStrings.current.settingsItemPushNotificationState,
                                value = uiState.pushNotificationState.toReadableName(),
                                onTap =
                                {
                                    pushNotificationDistributorBottomSheetOpened = true
                                }.takeIf {
                                    uiState.pushNotificationState ==
                                        PushNotificationManagerState.NoDistributorSelected
                                },
                            )
                        }
                    }
                    SettingsRow(
                        title = LocalStrings.current.settingsItemConversationReplyDepth,
                        value = uiState.replyDepth.toString(),
                        onTap = {
                            replyDepthBottomSheepOpened = true
                        },
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsItemTranslationProvider,
                        value = uiState.translationProviderConfigs
                            .firstOrNull { it.default }?.url
                            ?.replace("http://", "")
                            ?.replace("https://", "") ?: LocalStrings.current.shortUnavailable,
                        onTap = {
                            manageTranslationProvidersOpened = true
                        },
                    )

                    // Look & feel section
                    SettingsHeader(
                        title = LocalStrings.current.settingsHeaderLookAndFeel,
                        icon = LocalResources.current.styleFill,
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsItemTimelineLayout,
                        value = uiState.timelineLayout.toReadableName(),
                        onTap = {
                            timelineLayoutBottomSheetOpened = true
                        },
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsItemTheme,
                        value = uiState.theme.toReadableName(),
                        onTap = {
                            themeBottomSheetOpened = true
                        },
                    )
                    SettingsMultiColorRow(
                        title = LocalStrings.current.settingsItemCommentBarTheme,
                        values = themeRepository.getCommentBarColors(uiState.commentBarTheme),
                        onTap = {
                            commentBarThemeBottomSheetOpened = true
                        },
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsItemFontFamily,
                        value = uiState.fontFamily.toReadableName(),
                        onTap = {
                            fontFamilyBottomSheetOpened = true
                        },
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsItemFontScale,
                        value = uiState.fontScale.toReadableName(),
                        onTap = {
                            fontScaleBottomSheetOpened = true
                        },
                    )
                    SettingsColorRow(
                        title = LocalStrings.current.settingsItemThemeColor,
                        subtitle = LocalStrings.current.settingsItemThemeColorSubtitle,
                        value = uiState.themeColor ?: MaterialTheme.colorScheme.primary,
                        onTap = {
                            themeColorBottomSheetOpened = true
                        },
                    )
                    if (uiState.supportsDynamicColors) {
                        SettingsSwitchRow(
                            title = LocalStrings.current.settingsItemDynamicColors,
                            subtitle = LocalStrings.current.settingsItemDynamicColorsSubtitle,
                            value = uiState.dynamicColors,
                            onValueChange = {
                                model.reduce(SettingsMviModel.Intent.ChangeDynamicColors(it))
                            },
                        )
                    }
                    SettingsSwitchRow(
                        title = LocalStrings.current.settingsItemHideNavigationBarWhileScrolling,
                        value = uiState.hideNavigationBarWhileScrolling,
                        onValueChange = {
                            model.reduce(
                                SettingsMviModel.Intent.ChangeHideNavigationBarWhileScrolling(it),
                            )
                        },
                    )
                    if (uiState.appIconChangeSupported) {
                        SettingsRow(
                            title = LocalStrings.current.settingsItemAppIcon,
                            subtitle =
                            if (uiState.appIconRestartRequired) {
                                LocalStrings.current.messageRestartToApplyChanges
                            } else {
                                null
                            },
                            value = uiState.appIconVariant.toReadableName(),
                            onTap = {
                                appIconBottomSheetOpened = true
                            },
                        )
                    }
                    if (uiState.isBarThemeSupported) {
                        SettingsRow(
                            title = LocalStrings.current.settingsItemBarTheme,
                            value = uiState.barTheme.toReadableName(),
                            onTap = {
                                barThemeBottomSheetOpened = true
                            },
                        )
                    }

                    // NSFW section
                    SettingsHeader(
                        icon = LocalResources.current.explicitFill,
                        title = LocalStrings.current.settingsHeaderNsfw,
                    )
                    if (uiState.isLogged) {
                        SettingsRow(
                            title = LocalStrings.current.settingsItemBlockedAndMuted,
                            disclosureIndicator = true,
                            onTap = {
                                mainRouter.openBlockedAndMuted()
                            },
                        )
                    }
                    SettingsSwitchRow(
                        title = LocalStrings.current.settingsItemIncludeNsfw,
                        value = uiState.includeNsfw,
                        onValueChange = {
                            model.reduce(SettingsMviModel.Intent.ChangeIncludeNsfw(it))
                        },
                    )
                    SettingsSwitchRow(
                        title = LocalStrings.current.settingsItemBlurNsfw,
                        value = uiState.blurNsfw,
                        onValueChange = {
                            model.reduce(SettingsMviModel.Intent.ChangeBlurNsfw(it))
                        },
                    )

                    // Debug section
                    SettingsHeader(
                        icon = LocalResources.current.bugReport,
                        title = LocalStrings.current.settingsSectionDebug,
                    )
                    SettingsRow(
                        title = LocalStrings.current.settingsAbout,
                        onTap = {
                            aboutDialogOpened = true
                        },
                    )
                    SettingsSwitchRow(
                        title = LocalStrings.current.settingsItemCrashReportEnabled,
                        subtitle =
                        if (uiState.crashReportRestartRequired) {
                            LocalStrings.current.messageRestartToApplyChanges
                        } else {
                            null
                        },
                        value = uiState.crashReportEnabled,
                        onValueChange = {
                            model.reduce(SettingsMviModel.Intent.ChangeCrashReportEnabled(it))
                        },
                    )

                    // Other section
                    if (uiState.supportSettingsImportExport) {
                        SettingsHeader(
                            icon = LocalResources.current.handymanFill,
                            title = LocalStrings.current.itemOther,
                        )
                        SettingsRow(
                            title = LocalStrings.current.settingsItemExport,
                            onTap = {
                                model.reduce(SettingsMviModel.Intent.ExportSettings)
                            },
                        )
                        SettingsRow(
                            title = LocalStrings.current.settingsItemImport,
                            onTap = {
                                fileInputOpened = true
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.xxxl))
                }
            }
        },
    )

    if (uiState.loading) {
        ProgressHud()
    }

    if (languageBottomSheetOpened) {
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemLanguage,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            items =
            Locales.AVAILABLE_LANGUAGES.map { lang ->
                CustomModalBottomSheetItem(
                    label = lang.toLanguageName().orEmpty(),
                    trailingContent = {
                        Text(
                            text = lang.toLanguageFlag().orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                )
            },
            onSelect = { index ->
                languageBottomSheetOpened = false
                if (index != null) {
                    val value = Locales.AVAILABLE_LANGUAGES[index]
                    model.reduce(SettingsMviModel.Intent.ChangeLanguage(value))
                }
            },
        )
    }

    if (themeBottomSheetOpened) {
        val themes = listOf(UiTheme.Light, UiTheme.Dark, UiTheme.Black, UiTheme.Default)
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemTheme,
            items =
            themes.map { theme ->
                CustomModalBottomSheetItem(
                    label = theme.toReadableName(),
                    trailingContent = {
                        Icon(
                            modifier = Modifier.size(IconSize.m),
                            imageVector = theme.toIcon(LocalResources.current),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                )
            },
            onSelect = { index ->
                themeBottomSheetOpened = false
                if (index != null) {
                    val value = themes[index]
                    model.reduce(SettingsMviModel.Intent.ChangeTheme(value))
                }
            },
        )
    }

    if (fontFamilyBottomSheetOpened) {
        val fonts =
            listOf(
                UiFontFamily.AtkinsonHyperlegible,
                UiFontFamily.Exo2,
                UiFontFamily.NotoSans,
                UiFontFamily.Default,
            )
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemFontFamily,
            items =
            fonts.map { family ->
                CustomModalBottomSheetItem(
                    label = family.toReadableName(),
                    customLabelStyle = family.toTypography().bodyLarge,
                )
            },
            onSelect = { index ->
                fontFamilyBottomSheetOpened = false
                if (index != null) {
                    val value = fonts[index]
                    model.reduce(SettingsMviModel.Intent.ChangeFontFamily(value))
                }
            },
        )
    }

    if (fontScaleBottomSheetOpened) {
        val fontScales =
            listOf(
                UiFontScale.Largest,
                UiFontScale.Larger,
                UiFontScale.Normal,
                UiFontScale.Smaller,
                UiFontScale.Smallest,
            )
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemFontScale,
            items =
            fontScales.map {
                CustomModalBottomSheetItem(
                    label = it.toReadableName(),
                    customLabelStyle =
                    MaterialTheme.typography.bodyLarge.let { s ->
                        s.copy(fontSize = s.fontSize * it.toScaleFactor())
                    },
                )
            },
            onSelect = { index ->
                fontScaleBottomSheetOpened = false
                if (index != null) {
                    val scale = fontScales[index]
                    model.reduce(SettingsMviModel.Intent.ChangeFontScale(scale))
                }
            },
        )
    }

    if (themeColorBottomSheetOpened) {
        val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        CustomModalBottomSheet(
            sheetState = state,
            title = LocalStrings.current.settingsItemTheme,
            items =
            buildList {
                this +=
                    uiState.availableThemeColors.map { theme ->
                        CustomModalBottomSheetItem(
                            leadingContent = {
                                Box(
                                    modifier =
                                    Modifier
                                        .padding(start = Spacing.xs)
                                        .size(IconSize.m)
                                        .background(
                                            color = theme.toColor(),
                                            shape = CircleShape,
                                        ),
                                )
                            },
                            label = theme.toReadableName(),
                            trailingContent = {
                                Text(
                                    text = theme.toEmoji(),
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            },
                        )
                    }
                this +=
                    CustomModalBottomSheetItem(
                        leadingContent = {
                            Box(
                                modifier =
                                Modifier
                                    .padding(start = Spacing.xs)
                                    .size(IconSize.m)
                                    .background(
                                        color =
                                        uiState.themeColor
                                            ?: MaterialTheme.colorScheme.primary,
                                        shape = CircleShape,
                                    ),
                            )
                        },
                        label = LocalStrings.current.customOption,
                        trailingContent = {
                            Text(
                                text = "🎨",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        },
                    )
            },
            onSelect = { index ->
                themeColorBottomSheetOpened = false
                if (index != null) {
                    if (index in uiState.availableThemeColors.indices) {
                        // theme color selected
                        val value = uiState.availableThemeColors[index]
                        model.reduce(SettingsMviModel.Intent.ChangeThemeColor(value.toColor()))
                    } else {
                        // custom color selected
                        customColorPickerDialogOpened = true
                    }
                }
            },
        )
    }

    if (customColorPickerDialogOpened) {
        CustomColorPickerDialog(
            initialValue = uiState.themeColor ?: MaterialTheme.colorScheme.primary,
            onClose = { newColor ->
                customColorPickerDialogOpened = false
                if (newColor != null) {
                    model.reduce(SettingsMviModel.Intent.ChangeThemeColor(newColor))
                }
            },
        )
    }

    if (defaultTimelineTypeBottomSheetOpened) {
        CustomModalBottomSheet(
            title = LocalStrings.current.feedTypeTitle,
            items =
            uiState.availableTimelineTypes.map {
                CustomModalBottomSheetItem(
                    label = it.toReadableName(),
                    trailingContent = {
                        Icon(
                            modifier = Modifier.size(IconSize.m),
                            imageVector = it.toIcon(LocalResources.current),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                )
            },
            onSelect = { index ->
                defaultTimelineTypeBottomSheetOpened = false
                if (index != null) {
                    val type = uiState.availableTimelineTypes[index]
                    model.reduce(SettingsMviModel.Intent.ChangeDefaultTimelineType(type))
                }
            },
        )
    }

    if (urlOpeningModeBottomSheetOpened) {
        val types = uiState.availableUrlOpeningModes
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemUrlOpeningMode,
            items = types.map { CustomModalBottomSheetItem(label = it.toReadableName()) },
            onSelect = { index ->
                urlOpeningModeBottomSheetOpened = false
                if (index != null) {
                    val type = types[index]
                    model.reduce(SettingsMviModel.Intent.ChangeUrlOpeningMode(type))
                }
            },
        )
    }

    if (defaultPostVisibilityBottomSheetOpened) {
        val types = uiState.availableVisibilities
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemDefaultPostVisibility,
            items =
            types.map {
                CustomModalBottomSheetItem(
                    label = it.toReadableName(),
                    trailingContent = {
                        Icon(
                            modifier = Modifier.size(IconSize.m),
                            imageVector = it.toIcon(LocalResources.current),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                )
            },
            onSelect = { index ->
                defaultPostVisibilityBottomSheetOpened = false
                if (index != null) {
                    val type = types[index]
                    model.reduce(SettingsMviModel.Intent.ChangeDefaultPostVisibility(type))
                }
            },
        )
    }

    if (defaultReplyVisibilityBottomSheetOpened) {
        val types = uiState.availableVisibilities
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemDefaultReplyVisibility,
            items =
            types.map {
                CustomModalBottomSheetItem(
                    label = it.toReadableName(),
                    trailingContent = {
                        Icon(
                            modifier = Modifier.size(IconSize.m),
                            imageVector = it.toIcon(LocalResources.current),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                )
            },
            onSelect = { index ->
                defaultReplyVisibilityBottomSheetOpened = false
                if (index != null) {
                    val type = types[index]
                    model.reduce(SettingsMviModel.Intent.ChangeDefaultReplyVisibility(type))
                }
            },
        )
    }

    if (markupModeBottomSheetOpened) {
        val modes = uiState.availableMarkupModes
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemMarkupMode,
            items = modes.map { CustomModalBottomSheetItem(label = it.toReadableName()) },
            onSelect = { index ->
                markupModeBottomSheetOpened = false
                if (index != null) {
                    val mode = modes[index]
                    model.reduce(SettingsMviModel.Intent.ChangeMarkupMode(mode))
                }
            },
        )
    }

    if (maxPostBodyLinesBottomSheetOpened) {
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemMaxPostBodyLines,
            items = MAX_POST_BODY_LINES_OPTIONS.map {
                CustomModalBottomSheetItem(label = it.toMaxBodyLinesReadableName())
            },
            onSelect = { index ->
                maxPostBodyLinesBottomSheetOpened = false
                if (index != null) {
                    val value = MAX_POST_BODY_LINES_OPTIONS[index]
                    model.reduce(SettingsMviModel.Intent.ChangeMaxPostBodyLines(value))
                }
            },
        )
    }

    if (backgroundNotificationCheckIntervalDialogOpened) {
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsOptionBackgroundNotificationCheck,
            items =
            BACKGROUND_NOTIFICATION_CHECK_INTERVALS.map {
                CustomModalBottomSheetItem(
                    label =
                    it?.getPrettyDuration(
                        secondsLabel = LocalStrings.current.timeSecondShort,
                        minutesLabel = LocalStrings.current.timeMinuteShort,
                        hoursLabel = LocalStrings.current.timeHourShort,
                        daysLabel = LocalStrings.current.dateDayShort,
                        finePrecision = false,
                    ) ?: LocalStrings.current.durationNever,
                )
            },
            onSelect = { index ->
                backgroundNotificationCheckIntervalDialogOpened = false
                if (index != null) {
                    val value = BACKGROUND_NOTIFICATION_CHECK_INTERVALS[index]
                    model.reduce(
                        SettingsMviModel.Intent.ChangeBackgroundNotificationCheckInterval(value),
                    )
                }
            },
        )
    }

    if (notificationModeBottomSheetOpened) {
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemNotificationMode,
            items =
            uiState.availableNotificationModes.map {
                CustomModalBottomSheetItem(
                    label = it.toReadableName(),
                    trailingContent = {
                        Text(
                            text =
                            when (it) {
                                NotificationMode.Disabled -> ""
                                NotificationMode.Pull ->
                                    LocalStrings.current.settingsNotificationModePullExplanation

                                NotificationMode.Push ->
                                    buildString {
                                        append(LocalStrings.current.settingsNotificationModePushExplanation)
                                        append(" (")
                                        append(LocalStrings.current.experimental)
                                        append(")")
                                    }
                            },
                        )
                    },
                )
            },
            onSelect = { index ->
                notificationModeBottomSheetOpened = false
                if (index != null) {
                    val mode = uiState.availableNotificationModes[index]
                    model.reduce(SettingsMviModel.Intent.ChangeNotificationMode(mode))
                }
            },
        )
    }

    if (pushNotificationDistributorBottomSheetOpened) {
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsPushNotificationStateNoDistributorSelected,
            items =
            uiState.availablePushDistributors.map {
                CustomModalBottomSheetItem(label = it)
            },
            onSelect = { index ->
                pushNotificationDistributorBottomSheetOpened = false
                if (index != null) {
                    val distributor = uiState.availablePushDistributors[index]
                    model.reduce(SettingsMviModel.Intent.SelectPushDistributor(distributor))
                }
            },
        )
    }

    if (imageLoadingModeBottomSheetOpened) {
        val values =
            listOf(
                ImageLoadingMode.Always,
                ImageLoadingMode.OnDemand,
                ImageLoadingMode.OnWifi,
            )
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsAutoloadImages,
            items =
            values.map {
                CustomModalBottomSheetItem(
                    label = it.toReadableName(),
                )
            },
            onSelect = { index ->
                imageLoadingModeBottomSheetOpened = false
                if (index != null) {
                    model.reduce(
                        SettingsMviModel.Intent.ChangeAutoloadImages(values[index]),
                    )
                }
            },
        )
    }

    if (appIconBottomSheetOpened) {
        val values =
            listOf(
                AppIconVariant.Default,
                AppIconVariant.Alt,
            )
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemAppIcon,
            items =
            values.map { value ->
                CustomModalBottomSheetItem(
                    leadingContent = {
                        val painter =
                            when (value) {
                                AppIconVariant.Alt -> LocalResources.current.appIconAlt
                                else -> LocalResources.current.appIconDefault
                            }
                        Image(
                            modifier = Modifier.size(IconSize.m),
                            painter = painter,
                            contentDescription = null,
                        )
                    },
                    label = value.toReadableName(),
                )
            },
            onSelect = { index ->
                appIconBottomSheetOpened = false
                if (index != null) {
                    val value = values[index]
                    model.reduce(SettingsMviModel.Intent.ChangeAppIcon(value))
                }
            },
        )
    }

    if (fileInputOpened) {
        fileSystemManager.readFromFile(mimeTypes = arrayOf(SETTINGS_MIME_TYPE)) { content ->
            if (content != null) {
                model.reduce(SettingsMviModel.Intent.ImportSettings(content))
            }
            fileInputOpened = false
        }
    }

    if (barThemeBottomSheetOpened) {
        val values =
            listOf(
                UiBarTheme.Transparent,
                UiBarTheme.Opaque,
                UiBarTheme.Solid,
            )
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemBarTheme,
            items =
            values.map { CustomModalBottomSheetItem(label = it.toReadableName()) },
            onSelect = { index ->
                barThemeBottomSheetOpened = false
                if (index != null) {
                    model.reduce(
                        SettingsMviModel.Intent.ChangeBarTheme(values[index]),
                    )
                }
            },
        )
    }

    if (commentBarThemeBottomSheetOpened) {
        val values =
            listOf(
                CommentBarTheme.Rainbow,
                CommentBarTheme.Blue,
                CommentBarTheme.Green,
                CommentBarTheme.Red,
            )
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemCommentBarTheme,
            items = values.map { barTheme ->
                val colors = themeRepository.getCommentBarColors(barTheme)
                CustomModalBottomSheetItem(
                    leadingContent = {
                        MultiColorPreview(
                            modifier =
                            Modifier
                                .padding(start = Spacing.xs)
                                .size(36.dp),
                            colors = colors,
                        )
                    },
                    label = barTheme.toReadableName(),
                    trailingContent = {
                        Text(
                            text = barTheme.toEmoji(),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                )
            },
            onSelect = { index ->
                commentBarThemeBottomSheetOpened = false
                if (index != null) {
                    model.reduce(
                        SettingsMviModel.Intent.ChangeCommentBarTheme(values[index]),
                    )
                }
            },
        )
    }

    if (timelineLayoutBottomSheetOpened) {
        val values =
            listOf(
                TimelineLayout.Full,
                TimelineLayout.Compact,
                TimelineLayout.DistractionFree,
                TimelineLayout.Card,
            )
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemTimelineLayout,
            items =
            values.map { CustomModalBottomSheetItem(label = it.toReadableName()) },
            onSelect = { index ->
                timelineLayoutBottomSheetOpened = false
                if (index != null) {
                    model.reduce(
                        SettingsMviModel.Intent.ChangeTimelineLayout(values[index]),
                    )
                }
            },
        )
    }

    settingsContent?.also { content ->
        fileSystemManager.writeToFile(
            mimeType = SETTINGS_MIME_TYPE,
            name = SETTINGS_FILE_NAME,
            data = content,
        ) { success ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (success) {
                        successMessage
                    } else {
                        errorMessage
                    },
                )
            }
            settingsContent = null
        }
    }

    if (replyDepthBottomSheepOpened) {
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemConversationReplyDepth,
            items = REPLY_DEPTH_VALUES.map { CustomModalBottomSheetItem(label = it.toString()) },
            onSelect = { index ->
                replyDepthBottomSheepOpened = false
                if (index != null) {
                    val value = REPLY_DEPTH_VALUES[index]
                    model.reduce(SettingsMviModel.Intent.ChangeReplyDepth(value))
                }
            },
        )
    }
    if (aboutDialogOpened) {
        AboutDialog(
            onClose = {
                aboutDialogOpened = false
            },
        )
    }

    if (manageTranslationProvidersOpened) {
        var optionsOffset by remember { mutableStateOf(Offset.Zero) }
        var optionsMenuOpen by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val items = uiState.translationProviderConfigs.map { config ->
            CustomModalBottomSheetItem(
                label = config.url,
                subtitle = config.name,
                leadingContent = {
                    RadioButton(
                        selected = config.default,
                        onClick = {},
                    )
                },
                trailingContent = {
                    val options =
                        buildList {
                            if (!config.default) {
                                this += OptionId.Delete.toOption()
                            }
                        }

                    Box {
                        if (options.isNotEmpty()) {
                            IconButton(
                                modifier = Modifier.onGloballyPositioned {
                                    optionsOffset = it.positionInParent()
                                }.clearAndSetSemantics { },
                                onClick = {
                                    optionsMenuOpen = true
                                },
                            ) {
                                Icon(
                                    imageVector = LocalResources.current.moreVert,
                                    contentDescription = LocalStrings.current.moreInfo,
                                )
                            }
                        }

                        CustomDropDown(
                            expanded = optionsMenuOpen,
                            onDismiss = {
                                optionsMenuOpen = false
                            },
                            offset =
                            with(LocalDensity.current) {
                                DpOffset(
                                    x = optionsOffset.x.toDp(),
                                    y = optionsOffset.y.toDp(),
                                )
                            },
                        ) {
                            for (option in options) {
                                DropdownMenuItem(
                                    text = {
                                        Text(option.label)
                                    },
                                    onClick = {
                                        optionsMenuOpen = false
                                        when (option.id) {
                                            OptionId.Delete -> {
                                                translationProviderConfigToDelete = config
                                            }

                                            else -> Unit
                                        }
                                    },
                                )
                            }
                        }
                    }
                },
            )
        } + CustomModalBottomSheetItem(
            label = LocalStrings.current.actionAddNew,
            leadingContent = {
                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        imageVector = LocalResources.current.addCircle,
                        contentDescription = LocalStrings.current.actionAddNew,
                    )
                }
            },
        )
        CustomModalBottomSheet(
            title = LocalStrings.current.settingsItemTranslationProvider,
            sheetState = sheetState,
            items = items,
            shouldHideOnSelect = { index ->
                // do not hide the bottom sheet when selection the last "add" action
                index in uiState.translationProviderConfigs.indices
            },
            onSelect = { index ->
                if (index != null) {
                    val configs = uiState.translationProviderConfigs
                    if (index in configs.indices) {
                        manageTranslationProvidersOpened = false
                        val selectedConfig = configs[index]
                        model.reduce(SettingsMviModel.Intent.SwitchDefaultTranslationProvider(selectedConfig))
                    } else {
                        addTranslationProviderConfigDialogOpened = true
                    }
                } else {
                    manageTranslationProvidersOpened = false
                }
            },
        )
    }

    if (addTranslationProviderConfigDialogOpened) {
        EditTwoTextualInfosDialog(
            title = LocalStrings.current.translationProviderConfigDialogTitle,
            label1 = LocalStrings.current.translationProviderConfigFieldServerUrl,
            placeHolder1 = buildString {
                append(LocalStrings.current.exempliGratia)
                append(" ")
                append("https://libretranslate.com")
            },
            label2 = LocalStrings.current.translationProviderConfigFieldApiKey,
            keyboardType1 = KeyboardType.Uri,
            keyboardType2 = KeyboardType.Text,
            onClose = { url, key ->
                addTranslationProviderConfigDialogOpened = false
                if (url != null && key != null) {
                    model.reduce(SettingsMviModel.Intent.AddTranslationProviderConfig(url = url, apiKey = key))
                }
            },
        )
    }

    if (translationProviderConfigToDelete != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDelete,
            onClose = { confirm ->
                val config = translationProviderConfigToDelete
                translationProviderConfigToDelete = null
                if (confirm && config != null) {
                    model.reduce(SettingsMviModel.Intent.DeleteTranslationProviderConfig(config))
                }
            },
        )
    }
}

private val MAX_POST_BODY_LINES_OPTIONS =
    listOf(
        Int.MAX_VALUE,
        5,
        10,
        25,
        50,
    )

private val BACKGROUND_NOTIFICATION_CHECK_INTERVALS =
    listOf(
        null,
        10.minutes,
        30.minutes,
        1.hours,
        4.hours,
    )

private val REPLY_DEPTH_VALUES =
    listOf(
        1,
        2,
        3,
        4,
        5,
    )

@Composable
private fun Int.toMaxBodyLinesReadableName(): String = when (this) {
    Int.MAX_VALUE -> LocalStrings.current.settingsOptionUnlimited
    else -> this.toString()
}

private const val SETTINGS_MIME_TYPE = "application/json"
private const val SETTINGS_FILE_NAME = "raccoon4friendica_settings.json"
