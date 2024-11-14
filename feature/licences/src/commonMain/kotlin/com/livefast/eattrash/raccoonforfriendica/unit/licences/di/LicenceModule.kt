package com.livefast.eattrash.raccoonforfriendica.unit.licences.di

import com.livefast.eattrash.raccoonforfriendica.unit.licences.LicencesMviModel
import com.livefast.eattrash.raccoonforfriendica.unit.licences.LicencesViewModel
import org.koin.dsl.module

val featureLicenceModule =
    module {
        factory<LicencesMviModel> {
            LicencesViewModel(
                settingsRepository = get(),
            )
        }
    }
