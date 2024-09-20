package com.livefast.eattrash.raccoonforfriendica.feaure.search.di

import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchMviModel
import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchViewModel
import org.koin.dsl.module

val featureSearchModule =
    module {
        factory<SearchMviModel> {
            SearchViewModel(
                paginationManager = get(),
                userRepository = get(),
                timelineEntryRepository = get(),
                settingsRepository = get(),
                identityRepository = get(),
                hapticFeedback = get(),
                notificationCenter = get(),
                imagePreloadManager = get(),
                blurHashRepository = get(),
            )
        }
    }
