package com.livefast.eattrash.raccoonforfriendica.feature.explore.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val exploreModule =
    DI.Module("ExploreModule") {
        bindViewModel {
            ExploreViewModel(
                paginationManager = instance(),
                userRepository = instance(),
                timelineEntryRepository = instance(),
                identityRepository = instance(),
                settingsRepository = instance(),
                hapticFeedback = instance(),
                imagePreloadManager = instance(),
                blurHashRepository = instance(),
                apiConfigurationRepository = instance(),
                accountRepository = instance(),
                instanceShortcutRepository = instance(),
                imageAutoloadObserver = instance(),
                toggleEntryFavorite = instance(),
                toggleEntryDislike = instance(),
                getTranslation = instance(),
                getInnerUrl = instance(),
                notificationCenter = instance(),
            )
        }
    }
