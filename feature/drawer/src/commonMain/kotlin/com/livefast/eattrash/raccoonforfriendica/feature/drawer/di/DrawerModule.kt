package com.livefast.eattrash.raccoonforfriendica.feature.drawer.di

import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.PermanentDrawerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val drawerModule = module {
    viewModel {
        DrawerViewModel(
            apiConfigurationRepository = get(),
            identityRepository = get(),
            supportedFeatureRepository = get(),
            credentialsRepository = get(),
            switchAccountUseCase = get(),
            emojiHelper = get(),
            imageAutoloadObserver = get(),
            accountRepository = get(),
        )
    }
    viewModel {
        PermanentDrawerViewModel(
            identityRepository = get(),
            inboxManager = get(),
            supportedFeatureRepository = get(),
        )
    }
}
