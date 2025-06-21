package com.livefast.eattrash.raccoonforfriendica.core.architecture.di

import androidx.lifecycle.ViewModelProvider
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

val viewModelFactoryModule = DI.Module("ViewModelFactoryModule") {
    bind<ViewModelProvider.Factory> {
        singleton {
            CustomViewModelFactory(injector = di)
        }
    }
}
