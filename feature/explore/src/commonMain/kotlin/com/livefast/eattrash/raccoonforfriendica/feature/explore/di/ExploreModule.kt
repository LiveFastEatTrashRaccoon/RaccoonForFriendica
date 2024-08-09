package com.livefast.eattrash.raccoonforfriendica.feature.explore.di

import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreViewModel
import org.koin.dsl.module

val featureExploreModule =
    module {
        factory<ExploreMviModel> {
            ExploreViewModel(
                paginationManager = get(),
            )
        }
    }
