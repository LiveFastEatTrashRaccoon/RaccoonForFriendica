package com.livefast.eattrash.raccoonforfriendica.feature.announcements.di

import com.livefast.eattrash.raccoonforfriendica.feature.announcements.AnnouncementsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.announcements.AnnouncementsViewModel
import org.koin.dsl.module

val featureAnnouncementsModule =
    module {
        factory<AnnouncementsMviModel> {
            AnnouncementsViewModel(
                identityRepository = get(),
                settingsRepository = get(),
                announcementRepository = get(),
                emojiRepository = get(),
                announcementsManager = get(),
                imageAutoloadObserver = get(),
            )
        }
    }
