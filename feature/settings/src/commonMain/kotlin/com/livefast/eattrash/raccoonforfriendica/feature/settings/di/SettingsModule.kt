package com.livefast.eattrash.raccoonforfriendica.feature.settings.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.permissions.PermissionControllerWrapper
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class SettingsViewModelArgs(val controller: PermissionControllerWrapper)

val settingsModule = module {
    viewModel { params ->
        val args: SettingsViewModelArgs = params.get()
        SettingsViewModel(
            permissionController = args.controller,
            settingsRepository = get(),
            l10nManager = get(),
            themeRepository = get(),
            colorSchemeProvider = get(),
            themeColorRepository = get(),
            identityRepository = get(),
            supportedFeatureRepository = get(),
            circlesRepository = get(),
            pullNotificationManager = get(),
            pushNotificationManager = get(),
            crashReportManager = get(),
            appIconManager = get(),
            fileSystemManager = get(),
            importSettings = get(),
            exportSettings = get(),
            barColorProvider = get(),
            customTabsHelper = get(),
            translationProviderConfigStore = get(),
        )
    }
    viewModel {
        UserFeedbackViewModel(
            crashReportManager = get(),
        )
    }
}
