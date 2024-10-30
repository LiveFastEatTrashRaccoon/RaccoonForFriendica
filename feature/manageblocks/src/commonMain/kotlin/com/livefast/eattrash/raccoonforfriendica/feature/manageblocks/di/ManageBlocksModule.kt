package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.di

import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.ManageBlocksMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.ManageBlocksViewModel
import org.koin.dsl.module

val featureManageBlocksModule =
    module {
        factory<ManageBlocksMviModel> {
            ManageBlocksViewModel(
                paginationManager = get(),
                userRepository = get(),
                settingsRepository = get(),
                imagePreloadManager = get(),
            )
        }
    }
