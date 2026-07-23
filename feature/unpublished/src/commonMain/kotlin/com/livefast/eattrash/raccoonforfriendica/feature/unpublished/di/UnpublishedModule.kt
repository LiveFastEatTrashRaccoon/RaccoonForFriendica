package com.livefast.eattrash.raccoonforfriendica.feature.unpublished.di

import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val unpublishedModule = module {
    viewModel {
        UnpublishedViewModel(
            paginationManager = get(),
            identityRepository = get(),
            settingsRepository = get(),
            scheduledEntryRepository = get(),
            draftRepository = get(),
            imagePreloadManager = get(),
            blurHashRepository = get(),
            imageAutoloadObserver = get(),
            notificationCenter = get(),
        )
    }
}
