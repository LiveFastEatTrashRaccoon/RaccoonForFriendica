package com.livefast.eattrash.raccoonforfriendica.feature.followrequests.di

import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val followRequestsModule =
    DI.Module("FollowRequestsModule") {
        bind<FollowRequestsMviModel> {
            provider {
                FollowRequestsViewModel(
                    paginationManager = instance(),
                    userRepository = instance(),
                    settingsRepository = instance(),
                    imageAutoloadObserver = instance(),
                )
            }
        }
    }
