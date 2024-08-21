package com.livefast.eattrash.raccoonforfriendica.feature.circles.di

import com.livefast.eattrash.raccoonforfriendica.feature.circles.detail.CircleDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.detail.CircleDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.domain.ContactCache
import com.livefast.eattrash.raccoonforfriendica.feature.circles.domain.DefaultContactCache
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import org.koin.dsl.module

val featureCirclesModule =
    module {
        single<ContactCache> {
            DefaultContactCache(
                identityRepository = get(),
                userPaginationManager = get(),
            )
        }
        factory<CirclesMviModel> {
            CirclesViewModel(
                circlesRepository = get(),
                contactCache = get(),
            )
        }
        factory<CircleDetailMviModel> { params ->
            CircleDetailViewModel(
                id = params[0],
                paginationManager = get(),
                circlesRepository = get(),
                contactCache = get(),
            )
        }
    }
