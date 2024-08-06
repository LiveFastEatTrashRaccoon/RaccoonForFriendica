package com.livefast.eattrash.raccoonforfriendica.feature.settings.di

import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsViewModel
import org.koin.dsl.module

val featureSettingsModule =
    module {
        factory<SettingsMviModel> {
            SettingsViewModel(
                l10nManager = get(),
                themeRepository = get(),
            )
        }
    }
