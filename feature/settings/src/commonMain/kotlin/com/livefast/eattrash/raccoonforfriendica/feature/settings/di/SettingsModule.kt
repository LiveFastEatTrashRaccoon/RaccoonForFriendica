package com.livefast.eattrash.raccoonforfriendica.feature.settings.di

import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val settingsModule =
    DI.Module("SettingsModule") {
        bind<SettingsMviModel> {
            provider {
                SettingsViewModel(
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
