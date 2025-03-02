package com.livefast.eattrash.raccoonforfriendica.feaure.search.di

import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchMviModel
import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val searchModule =
    DI.Module("SearchModule") {
        bind<SearchMviModel> {
            provider {
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
    }
