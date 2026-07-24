package com.livefast.eattrash.raccoonforfriendica.feature.timeline.di

import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val timelineModule = module {
    viewModel {
        TimelineViewModel(
            paginationManager = get(),
            identityRepository = get(),
            activeAccountMonitor = get(),
            apiConfigurationRepository = get(),
            timelineEntryRepository = get(),
            settingsRepository = get(),
            userRepository = get(),
            circlesRepository = get(),
            hapticFeedback = get(),
            imagePreloadManager = get(),
            blurHashRepository = get(),
            accountRepository = get(),
            instanceShortcutRepository = get(),
            imageAutoloadObserver = get(),
            announcementsManager = get(),
            toggleEntryFavorite = get(),
            toggleEntryDislike = get(),
            getTranslation = get(),
            getInnerUrl = get(),
            timelineNavigationManager = get(),
            followedHashtagCache = get(),
            notificationCenter = get(),
        )
    }
}
