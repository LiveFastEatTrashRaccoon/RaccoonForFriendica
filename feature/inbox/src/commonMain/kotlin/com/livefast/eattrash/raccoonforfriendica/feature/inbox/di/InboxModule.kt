package com.livefast.eattrash.raccoonforfriendica.feature.inbox.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val inboxModule =
    DI.Module("InboxModule") {
        bindViewModel {
            InboxViewModel(
                paginationManager = instance(),
                userRepository = instance(),
                identityRepository = instance(),
                settingsRepository = instance(),
                notificationRepository = instance(),
                inboxManager = instance(),
                hapticFeedback = instance(),
                imagePreloadManager = instance(),
                blurHashRepository = instance(),
                markerRepository = instance(),
                pullNotificationManager = instance(),
                imageAutoloadObserver = instance(),
            )
        }
    }
