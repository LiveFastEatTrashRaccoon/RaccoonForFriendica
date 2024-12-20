package com.livefast.eattrash.raccoonforfriendica.feature.inbox.di

import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val inboxModule =
    DI.Module("InboxModule") {
        bind<InboxMviModel> {
            provider {
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
    }
