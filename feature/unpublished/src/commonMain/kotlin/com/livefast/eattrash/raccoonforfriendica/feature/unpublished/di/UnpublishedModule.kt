package com.livefast.eattrash.raccoonforfriendica.feature.unpublished.di

import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val unpublishedModule =
    DI.Module("UnpublishedModule") {
        bind<UnpublishedMviModel> {
            provider {
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
    }
