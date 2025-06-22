package com.livefast.eattrash.raccoonforfriendica.feaure.search.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val searchModule =
    DI.Module("SearchModule") {
        bindViewModel {
            SearchViewModel(
                paginationManager = instance(),
                userRepository = instance(),
                timelineEntryRepository = instance(),
                settingsRepository = instance(),
                identityRepository = instance(),
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
