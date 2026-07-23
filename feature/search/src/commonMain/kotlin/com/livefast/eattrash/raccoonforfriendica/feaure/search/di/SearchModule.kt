package com.livefast.eattrash.raccoonforfriendica.feaure.search.di

import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    viewModel {
        SearchViewModel(
            paginationManager = get(),
            userRepository = get(),
            timelineEntryRepository = get(),
            settingsRepository = get(),
            identityRepository = get(),
            hapticFeedback = get(),
            imagePreloadManager = get(),
            blurHashRepository = get(),
            apiConfigurationRepository = get(),
            accountRepository = get(),
            instanceShortcutRepository = get(),
            imageAutoloadObserver = get(),
            toggleEntryFavorite = get(),
            toggleEntryDislike = get(),
            getTranslation = get(),
            getInnerUrl = get(),
            notificationCenter = get(),
        )
    }
}
