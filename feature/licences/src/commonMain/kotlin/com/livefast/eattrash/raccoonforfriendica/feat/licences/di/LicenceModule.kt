package com.livefast.eattrash.raccoonforfriendica.feat.licences.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feat.licences.LicencesViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val licenceModule =
    DI.Module("LicenceModule") {
        bindViewModel {
            LicencesViewModel(
                settingsRepository = instance(),
            )
        }
    }
