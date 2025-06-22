package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.main.MainViewModel
import org.kodein.di.DI
import org.kodein.di.instance

internal val mainModule =
    DI.Module("MainModule") {
        bindViewModel {
            MainViewModel(
                inboxManager = instance(),
            )
        }
    }
