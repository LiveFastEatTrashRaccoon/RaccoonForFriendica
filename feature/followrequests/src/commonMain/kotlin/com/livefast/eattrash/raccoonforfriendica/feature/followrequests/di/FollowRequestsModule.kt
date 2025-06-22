package com.livefast.eattrash.raccoonforfriendica.feature.followrequests.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val followRequestsModule =
    DI.Module("FollowRequestsModule") {
        bindViewModel {
            FollowRequestsViewModel(
                paginationManager = instance(),
                userRepository = instance(),
                settingsRepository = instance(),
                imageAutoloadObserver = instance(),
            )
        }
    }
