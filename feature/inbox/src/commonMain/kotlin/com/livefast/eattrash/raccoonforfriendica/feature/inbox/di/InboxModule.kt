package com.livefast.eattrash.raccoonforfriendica.feature.inbox.di

import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val inboxModule = module {
    viewModel {
        InboxViewModel(
            paginationManager = get(),
            userRepository = get(),
            identityRepository = get(),
            settingsRepository = get(),
            notificationRepository = get(),
            entryRepository = get(),
            inboxManager = get(),
            hapticFeedback = get(),
            imagePreloadManager = get(),
            blurHashRepository = get(),
            markerRepository = get(),
            pullNotificationManager = get(),
            imageAutoloadObserver = get(),
        )
    }
}
