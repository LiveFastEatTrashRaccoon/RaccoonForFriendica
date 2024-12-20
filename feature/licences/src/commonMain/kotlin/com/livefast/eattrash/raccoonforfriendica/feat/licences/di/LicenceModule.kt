package com.livefast.eattrash.raccoonforfriendica.feat.licences.di

import com.livefast.eattrash.raccoonforfriendica.feat.licences.LicencesMviModel
import com.livefast.eattrash.raccoonforfriendica.feat.licences.LicencesViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val licenceModule =
    DI.Module("LicenceModule") {
        bind<LicencesMviModel> {
            provider {
                LicencesViewModel(
                    settingsRepository = instance(),
                )
            }
        }
    }
