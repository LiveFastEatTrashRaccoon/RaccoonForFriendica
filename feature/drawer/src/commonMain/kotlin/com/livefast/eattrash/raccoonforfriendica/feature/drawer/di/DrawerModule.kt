package com.livefast.eattrash.raccoonforfriendica.feature.drawer.di

import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerViewModel
import org.koin.dsl.module

val featureDrawerModule =
    module {
        single<DrawerMviModel> {
            DrawerViewModel(
                apiConfigurationRepository = get(),
                identityRepository = get(),
                nodeInfoRepository = get(),
            )
        }
    }
