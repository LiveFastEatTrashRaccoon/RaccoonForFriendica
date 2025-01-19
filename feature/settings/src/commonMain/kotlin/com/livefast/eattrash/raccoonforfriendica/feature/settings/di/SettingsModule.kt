package com.livefast.eattrash.raccoonforfriendica.feature.settings.di

import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackViewModel
import dev.icerock.moko.permissions.PermissionsController
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

val settingsModule =
    DI.Module("SettingsModule") {
        bind<SettingsMviModel> {
            factory { controller: PermissionsController ->
                SettingsViewModel(
                    permissionController = controller,
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
                )
            }
        }
        bind<UserFeedbackMviModel> {
            provider {
                UserFeedbackViewModel(
                    crashReportManager = instance(),
                )
            }
        }
    }
