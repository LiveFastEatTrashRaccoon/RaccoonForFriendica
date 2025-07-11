package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.ManageBlocksViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val manageBlocksModule =
    DI.Module("ManageBlocksModule") {
        bindViewModel {
            ManageBlocksViewModel(
                paginationManager = instance(),
                userRepository = instance(),
                settingsRepository = instance(),
                accountRepository = instance(),
                userRateLimitRepository = instance(),
                imagePreloadManager = instance(),
                imageAutoloadObserver = instance(),
                stopWordRepository = instance(),
            )
        }
    }
