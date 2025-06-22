package com.livefast.eattrash.raccoonforfriendica.feature.announcements.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.announcements.AnnouncementsViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val announcementsModule =
    DI.Module("AnnouncementsModule") {
        bindViewModel {
            AnnouncementsViewModel(
                identityRepository = instance(),
                settingsRepository = instance(),
                announcementRepository = instance(),
                emojiRepository = instance(),
                announcementsManager = instance(),
                imageAutoloadObserver = instance(),
            )
        }
    }
