package com.livefast.eattrash.raccoonforfriendica.feature.explore.di

import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val exploreModule =
    DI.Module("ExploreModule") {
        bind<ExploreMviModel> {
            provider {
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
                    notificationCenter = instance(),
                )
            }
        }
    }
