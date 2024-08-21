package com.livefast.eattrash.raccoonforfriendica.feature.circles.di

import com.livefast.eattrash.raccoonforfriendica.feature.circles.detail.CircleDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.detail.CircleDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.domain.CirclesCache
import com.livefast.eattrash.raccoonforfriendica.feature.circles.domain.DefaultCirclesCache
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import org.koin.dsl.module

val featureCirclesModule =
    module {
        single<CirclesCache> {
            DefaultCirclesCache()
        }
        factory<CirclesMviModel> {
            CirclesViewModel(
                circlesRepository = get(),
                circlesCache = get(),
            )
        }
        factory<CircleDetailMviModel> { params ->
            CircleDetailViewModel(
                id = params[0],
            )
        }
    }
