package com.livefast.eattrash.raccoonforfriendica.feature.circles.di

import com.livefast.eattrash.raccoonforfriendica.feature.circles.detail.CircleDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.detail.CircleDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import org.koin.dsl.module

val featureCirclesModule =
    module {
        factory<CirclesMviModel> {
            CirclesViewModel(
                circlesRepository = get(),
                nodeInfoRepository = get(),
            )
        }
        factory<CircleDetailMviModel> { params ->
            CircleDetailViewModel(
                id = params[0],
                paginationManager = get(),
                circlesRepository = get(),
                searchPaginationManager = get(),
                imagePreloadManager = get(),
            )
        }
    }
