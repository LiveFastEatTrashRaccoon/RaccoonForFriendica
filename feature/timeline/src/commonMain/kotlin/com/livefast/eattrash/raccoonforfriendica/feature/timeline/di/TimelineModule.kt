package com.livefast.eattrash.raccoonforfriendica.feature.timeline.di

import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val timelineModule =
    DI.Module("TimelineModule") {
        bind<TimelineMviModel> {
            provider {
                TimelineViewModel(
                    paginationManager = instance(),
                    identityRepository = instance(),
                    activeAccountMonitor = instance(),
                    apiConfigurationRepository = instance(),
                    timelineEntryRepository = instance(),
                    settingsRepository = instance(),
                    userRepository = instance(),
                    circlesRepository = instance(),
                    hapticFeedback = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    imageAutoloadObserver = instance(),
                    announcementsManager = instance(),
                    toggleEntryFavorite = instance(),
                    toggleEntryDislike = instance(),
                    getTranslation = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
