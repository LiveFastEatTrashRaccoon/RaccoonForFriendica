package com.livefast.eattrash.raccoonforfriendica.feature.favorites.di

import com.livefast.eattrash.raccoonforfriendica.feature.favorites.FavoritesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.FavoritesViewModel
import org.koin.dsl.module

val featureFavoritesModule =
    module {
        factory<FavoritesMviModel> { params ->
            FavoritesViewModel(
                type = params[0],
                paginationManager = get(),
                timelineEntryRepository = get(),
        )
    }
}
