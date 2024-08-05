package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.main.MainMviModel
import com.livefast.eattrash.raccoonforfriendica.main.MainViewModel
import org.koin.dsl.module

internal val sharedModule =
    module {
        factory<MainMviModel> {
            MainViewModel()
        }
    }
