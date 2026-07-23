package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.di

import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.ManageBlocksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val manageBlocksModule = module {
    viewModel {
        ManageBlocksViewModel(
            paginationManager = get(),
            userRepository = get(),
            settingsRepository = get(),
            accountRepository = get(),
            userRateLimitRepository = get(),
            imagePreloadManager = get(),
            imageAutoloadObserver = get(),
            stopWordRepository = get(),
        )
    }
}
