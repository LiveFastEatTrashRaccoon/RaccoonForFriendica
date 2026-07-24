package com.livefast.eattrash.raccoonforfriendica.feature.explore.di

import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val exploreModule = module {
    viewModel {
        ExploreViewModel(
            paginationManager = get(),
            userRepository = get(),
            timelineEntryRepository = get(),
            identityRepository = get(),
            settingsRepository = get(),
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
            credentialsRepository = get(),
            notificationCenter = get(),
        )
    }
}
