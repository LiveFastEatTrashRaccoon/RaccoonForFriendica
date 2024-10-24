package com.livefast.eattrash.raccoonforfriendica.feature.settings.di

import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackViewModel
import org.koin.dsl.module

val featureSettingsModule =
    module {
        factory<SettingsMviModel> {
            SettingsViewModel(
                l10nManager = get(),
                themeRepository = get(),
                settingsRepository = get(),
                colorSchemeProvider = get(),
                themeColorRepository = get(),
                identityRepository = get(),
                supportedFeatureRepository = get(),
                circlesRepository = get(),
                pullNotificationChecker = get(),
            )
        }
        factory<UserFeedbackMviModel> {
            UserFeedbackViewModel(
                crashReportManager = get(),
            )
        }
    }
