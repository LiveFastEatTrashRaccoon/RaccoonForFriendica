package com.livefast.eattrash.raccoonforfriendica.feature.announcements.di

import com.livefast.eattrash.raccoonforfriendica.feature.announcements.AnnouncementsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val announcementsModule = module {
    viewModel {
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
