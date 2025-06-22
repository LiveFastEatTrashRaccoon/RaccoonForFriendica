package com.livefast.eattrash.raccoonforfriendica.feature.settings.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackViewModel
import dev.icerock.moko.permissions.PermissionsController
import org.kodein.di.DI
import org.kodein.di.instance

data class SettingsViewModelArgs(val controller: PermissionsController) : ViewModelCreationArgs

val settingsModule =
    DI.Module("SettingsModule") {
        bindViewModelWithArgs { args: SettingsViewModelArgs ->
            SettingsViewModel(
                permissionController = args.controller,
                settingsRepository = instance(),
                l10nManager = instance(),
                themeRepository = instance(),
                colorSchemeProvider = instance(),
                themeColorRepository = instance(),
                identityRepository = instance(),
                supportedFeatureRepository = instance(),
                circlesRepository = instance(),
                pullNotificationManager = instance(),
                pushNotificationManager = instance(),
                crashReportManager = instance(),
                appIconManager = instance(),
                fileSystemManager = instance(),
                importSettings = instance(),
                exportSettings = instance(),
                barColorProvider = instance(),
                customTabsHelper = instance(),
            )
        }
        bindViewModel {
            UserFeedbackViewModel(
                crashReportManager = instance(),
            )
        }
    }
