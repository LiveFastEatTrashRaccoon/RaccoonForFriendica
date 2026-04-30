package com.livefast.eattrash.raccoonforfriendica.core.architecture.di

import androidx.lifecycle.ViewModelProvider
import org.kodein.di.DI
import org.kodein.di.bindSingleton

val viewModelFactoryModule = DI.Module("ViewModelFactoryModule") {
    bindSingleton<ViewModelProvider.Factory> {
        CustomViewModelFactory(injector = di)
    }
}
