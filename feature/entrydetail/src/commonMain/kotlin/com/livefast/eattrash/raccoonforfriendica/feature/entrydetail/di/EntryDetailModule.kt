package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di

import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailViewModel
import org.koin.dsl.module

val featureEntryDetailModule =
    module {
        factory<EntryDetailMviModel> { params ->
            EntryDetailViewModel(
                id = params[0],
                timelineEntryRepository = get(),
                identityRepository = get(),
                settingsRepository = get(),
                userRepository = get(),
                hapticFeedback = get(),
                notificationCenter = get(),
            )
        }
    }
