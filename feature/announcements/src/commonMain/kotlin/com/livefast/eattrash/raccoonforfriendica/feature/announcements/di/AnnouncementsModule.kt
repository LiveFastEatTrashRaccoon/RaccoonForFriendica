package com.livefast.eattrash.raccoonforfriendica.feature.announcements.di

import com.livefast.eattrash.raccoonforfriendica.feature.announcements.AnnouncementsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.announcements.AnnouncementsViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val announcementsModule =
    DI.Module("AnnouncementsModule") {
        bind<AnnouncementsMviModel> {
            provider {
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
    }
