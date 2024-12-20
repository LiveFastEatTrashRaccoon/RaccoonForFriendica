package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.main.MainMviModel
import com.livefast.eattrash.raccoonforfriendica.main.MainViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

internal val mainModule =
    DI.Module("MainModule") {
        bind<MainMviModel> {
            provider {
                MainViewModel(
                    inboxManager = instance(),
                )
            }
        }
    }
