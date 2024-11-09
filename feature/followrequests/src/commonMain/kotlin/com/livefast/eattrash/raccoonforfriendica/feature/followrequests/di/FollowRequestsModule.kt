package com.livefast.eattrash.raccoonforfriendica.feature.followrequests.di

import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsViewModel
import org.koin.dsl.module

val featureFollowRequestsModule =
    module {
        factory<FollowRequestsMviModel> {
            FollowRequestsViewModel(
                paginationManager = get(),
                userRepository = get(),
                imageAutoloadObserver = get(),
            )
        }
    }
