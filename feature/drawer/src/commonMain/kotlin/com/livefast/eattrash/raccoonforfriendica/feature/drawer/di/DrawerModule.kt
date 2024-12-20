package com.livefast.eattrash.raccoonforfriendica.feature.drawer.di

import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val drawerModule =
    DI.Module("DrawerModule") {
        bind<DrawerMviModel> {
            provider {
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
    }
