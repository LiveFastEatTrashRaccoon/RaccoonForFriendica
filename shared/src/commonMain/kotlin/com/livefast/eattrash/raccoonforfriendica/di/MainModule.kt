package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val mainModule = module {
    viewModel {
        MainViewModel(
            inboxManager = get(),
        )
    }
}
