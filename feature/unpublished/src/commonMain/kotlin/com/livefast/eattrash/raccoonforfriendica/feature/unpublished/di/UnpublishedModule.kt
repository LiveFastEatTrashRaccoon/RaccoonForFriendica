package com.livefast.eattrash.raccoonforfriendica.feature.unpublished.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val unpublishedModule =
    DI.Module("UnpublishedModule") {
        bindViewModel {
            UnpublishedViewModel(
                paginationManager = instance(),
                identityRepository = instance(),
                settingsRepository = instance(),
                scheduledEntryRepository = instance(),
                draftRepository = instance(),
                imagePreloadManager = instance(),
                blurHashRepository = instance(),
                imageAutoloadObserver = instance(),
                notificationCenter = instance(),
            )
        }
    }
