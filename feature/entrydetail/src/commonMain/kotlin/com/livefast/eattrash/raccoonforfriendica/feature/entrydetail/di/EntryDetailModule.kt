package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di

import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

val entryDetailModule =
    DI.Module("EntryDetailModule") {
        bind<EntryDetailMviModel> {
            factory { id: String ->
                EntryDetailViewModel(
                    id = id,
                    timelineEntryRepository = instance(),
                    identityRepository = instance(),
                    settingsRepository = instance(),
                    userRepository = instance(),
                    hapticFeedback = instance(),
                    entryCache = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    emojiHelper = instance(),
                    replyHelper = instance(),
                    imageAutoloadObserver = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
