package com.livefast.eattrash.raccoonforfriendica.feat.licences.di

import com.livefast.eattrash.raccoonforfriendica.feat.licences.LicencesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val licenceModule = module {
    viewModel {
        LicencesViewModel(
            settingsRepository = get(),
        )
    }
}
