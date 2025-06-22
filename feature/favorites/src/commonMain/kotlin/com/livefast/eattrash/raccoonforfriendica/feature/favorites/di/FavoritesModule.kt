package com.livefast.eattrash.raccoonforfriendica.feature.favorites.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.FavoritesViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class FavoritesViewModelArgs(val type: FavoritesType) : ViewModelCreationArgs

val favoritesModule =
    DI.Module("FavoritesModule") {
        bindViewModelWithArgs { args: FavoritesViewModelArgs ->
            FavoritesViewModel(
                type = args.type,
                paginationManager = instance(),
                timelineEntryRepository = instance(),
                settingsRepository = instance(),
                identityRepository = instance(),
                userRepository = instance(),
                apiConfigurationRepository = instance(),
                accountRepository = instance(),
                instanceShortcutRepository = instance(),
                blurHashRepository = instance(),
                hapticFeedback = instance(),
                imagePreloadManager = instance(),
                imageAutoloadObserver = instance(),
                toggleEntryFavorite = instance(),
                toggleEntryDislike = instance(),
                getTranslation = instance(),
                getInnerUrl = instance(),
                timelineNavigationManager = instance(),
                notificationCenter = instance(),
            )
        }
    }
