package com.livefast.eattrash.raccoonforfriendica.feature.favorites.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.FavoritesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.FavoritesViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

val favoritesModule =
    DI.Module("FavoritesModule") {
        bind<FavoritesMviModel> {
            factory { type: FavoritesType ->
                FavoritesViewModel(
                    type = type,
                    paginationManager = instance(),
                    timelineEntryRepository = instance(),
                    settingsRepository = instance(),
                    identityRepository = instance(),
                    userRepository = instance(),
                    hapticFeedback = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    imageAutoloadObserver = instance(),
                    toggleEntryFavorite = instance(),
                    toggleEntryDislike = instance(),
                    getTranslation = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
