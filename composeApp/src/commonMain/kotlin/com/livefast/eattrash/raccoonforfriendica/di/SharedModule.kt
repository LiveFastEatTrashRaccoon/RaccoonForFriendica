package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.main.MainMviModel
import com.livefast.eattrash.raccoonforfriendica.main.MainViewModel
import com.livefast.eattrash.raccoonforfriendica.navigation.DefaultDetailOpener
import org.koin.dsl.module

internal val sharedModule =
    module {
        factory<MainMviModel> {
            MainViewModel()
        }
        single<DetailOpener> {
            DefaultDetailOpener(
                navigationCoordinator = get(),
            )
        }
    }
