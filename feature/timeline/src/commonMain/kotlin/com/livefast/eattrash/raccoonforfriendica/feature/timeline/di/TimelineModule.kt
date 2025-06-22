package com.livefast.eattrash.raccoonforfriendica.feature.timeline.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val timelineModule =
    DI.Module("TimelineModule") {
        bindViewModel {
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
                accountRepository = instance(),
                instanceShortcutRepository = instance(),
                imageAutoloadObserver = instance(),
                announcementsManager = instance(),
                toggleEntryFavorite = instance(),
                toggleEntryDislike = instance(),
                getTranslation = instance(),
                getInnerUrl = instance(),
                timelineNavigationManager = instance(),
                followedHashtagCache = instance(),
                notificationCenter = instance(),
            )
        }
    }
