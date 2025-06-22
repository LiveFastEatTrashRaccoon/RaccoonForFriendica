package com.livefast.eattrash.raccoonforfriendica.feature.drawer.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val drawerModule =
    DI.Module("DrawerModule") {
        bindViewModel {
            DrawerViewModel(
                apiConfigurationRepository = instance(),
                identityRepository = instance(),
                supportedFeatureRepository = instance(),
                credentialsRepository = instance(),
                switchAccountUseCase = instance(),
                emojiHelper = instance(),
                imageAutoloadObserver = instance(),
                accountRepository = instance(),
            )
        }
    }
