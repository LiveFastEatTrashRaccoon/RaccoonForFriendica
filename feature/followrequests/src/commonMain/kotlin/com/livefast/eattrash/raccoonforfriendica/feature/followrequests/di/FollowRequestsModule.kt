package com.livefast.eattrash.raccoonforfriendica.feature.followrequests.di

import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val followRequestsModule = module {
    viewModel {
        FollowRequestsViewModel(
            paginationManager = get(),
            userRepository = get(),
            settingsRepository = get(),
            imageAutoloadObserver = get(),
        )
    }
}
