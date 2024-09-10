package com.livefast.eattrash.raccoonforfriendica.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Explicit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toColor
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toEmoji
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toScaleFactor
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toTypography
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomColorPickerDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsColorRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsSwitchRow
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.Locales
import com.livefast.eattrash.raccoonforfriendica.core.l10n.toLanguageFlag
import com.livefast.eattrash.raccoonforfriendica.core.l10n.toLanguageName
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.feature.settings.about.AboutDialog

class SettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<SettingsMviModel>()
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val detailOpener = remember { getDetailOpener() }
        var languageBottomSheetOpened by remember { mutableStateOf(false) }
        var themeBottomSheetOpened by remember { mutableStateOf(false) }
        var fontFamilyBottomSheetOpened by remember { mutableStateOf(false) }
        var fontScaleBottomSheetOpened by remember { mutableStateOf(false) }
        var themeColorBottomSheetOpened by remember { mutableStateOf(false) }
        var defaultTimelineTypeBottomSheetOpened by remember { mutableStateOf(false) }
        var urlOpeningModeBottomSheetOpened by remember { mutableStateOf(false) }
        var aboutDialogOpened by remember { mutableStateOf(false) }
        var customColorPickerDialogOpened by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            modifier = Modifier.padding(horizontal = Spacing.s),
                            text = LocalStrings.current.settingsTitle,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        if (navigationCoordinator.canPop.value) {
                            IconButton(
                                onClick = {
                                    navigationCoordinator.pop()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                )
            },
            content = { padding ->
                Box(
                    modifier =
                        Modifier
                            .padding(padding)
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                ) {
                    Column {
                        SettingsHeader(
                            title = LocalStrings.current.settingsHeaderGeneral,
                            icon = Icons.Default.Settings,
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
                            title = LocalStrings.current.settingsItemUrlOpeningMode,
                            value = uiState.urlOpeningMode.toReadableName(),
                            onTap = {
                                urlOpeningModeBottomSheetOpened = true
                            },
                        )

                        SettingsHeader(
                            title = LocalStrings.current.settingsHeaderLookAndFeel,
                            icon = Icons.Default.Style,
                        )
                        SettingsRow(
                            title = LocalStrings.current.settingsItemTheme,
                            value = uiState.theme.toReadableName(),
                            onTap = {
                                themeBottomSheetOpened = true
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
                                onValueChanged = {
                                    model.reduce(SettingsMviModel.Intent.ChangeDynamicColors(it))
                                },
                            )
                        }

                        SettingsHeader(
                            icon = Icons.Default.Explicit,
                            title = LocalStrings.current.settingsHeaderNsfw,
                        )
                        if (uiState.isLogged) {
                            SettingsRow(
                                title = LocalStrings.current.settingsItemBlockedAndMuted,
                                disclosureIndicator = true,
                                onTap = {
                                    detailOpener.openBlockedAndMuted()
                                },
                            )
                        }
                        SettingsSwitchRow(
                            title = LocalStrings.current.settingsItemIncludeNsfw,
                            value = uiState.includeNsfw,
                            onValueChanged = {
                                model.reduce(SettingsMviModel.Intent.ChangeIncludeNsfw(it))
                            },
                        )
                        SettingsSwitchRow(
                            title = LocalStrings.current.settingsItemBlurNsfw,
                            value = uiState.blurNsfw,
                            onValueChanged = {
                                model.reduce(SettingsMviModel.Intent.ChangeBlurNsfw(it))
                            },
                        )

                        SettingsHeader(
                            icon = Icons.Default.BugReport,
                            title = LocalStrings.current.settingsSectionDebug,
                        )
                        SettingsRow(
                            title = LocalStrings.current.settingsAbout,
                            disclosureIndicator = true,
                            onTap = {
                                aboutDialogOpened = true
                            },
                        )

                        Spacer(modifier = Modifier.height(Spacing.xxxl))
                    }
                }
            },
        )

        if (languageBottomSheetOpened) {
            val languages =
                listOf(
                    Locales.EN,
                    Locales.IT,
                )
            CustomModalBottomSheet(
                title = LocalStrings.current.settingsItemLanguage,
                items =
                    languages.map { lang ->
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
                onSelected = { index ->
                    languageBottomSheetOpened = false
                    if (index != null) {
                        val value = languages[index]
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
                                    imageVector = theme.toIcon(),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            },
                        )
                    },
                onSelected = { index ->
                    themeBottomSheetOpened = false
                    if (index != null) {
                        val value = themes[index]
                        model.reduce(SettingsMviModel.Intent.ChangeTheme(value))
                    }
                },
            )
        }

        if (fontFamilyBottomSheetOpened) {
            val fonts = listOf(UiFontFamily.Default, UiFontFamily.Exo2, UiFontFamily.NotoSans)
            CustomModalBottomSheet(
                title = LocalStrings.current.settingsItemFontFamily,
                items =
                    fonts.map { family ->
                        CustomModalBottomSheetItem(
                            label = family.toReadableName(),
                            customLabelStyle = family.toTypography().bodyLarge,
                        )
                    },
                onSelected = { index ->
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
                onSelected = { index ->
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
                onSelected = { index ->
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
                                    imageVector = it.toIcon(),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            },
                        )
                    },
                onSelected = { index ->
                    defaultTimelineTypeBottomSheetOpened = false
                    if (index != null) {
                        val type = uiState.availableTimelineTypes[index]
                        model.reduce(SettingsMviModel.Intent.ChangeDefaultTimelineType(type))
                    }
                },
            )
        }

        if (urlOpeningModeBottomSheetOpened) {
            val types =
                listOf(
                    UrlOpeningMode.External,
                    UrlOpeningMode.CustomTabs,
                )
            CustomModalBottomSheet(
                title = LocalStrings.current.settingsItemUrlOpeningMode,
                items = types.map { CustomModalBottomSheetItem(label = it.toReadableName()) },
                onSelected = { index ->
                    urlOpeningModeBottomSheetOpened = false
                    if (index != null) {
                        val type = types[index]
                        model.reduce(SettingsMviModel.Intent.ChangeUrlOpeningMode(type))
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
    }
}
