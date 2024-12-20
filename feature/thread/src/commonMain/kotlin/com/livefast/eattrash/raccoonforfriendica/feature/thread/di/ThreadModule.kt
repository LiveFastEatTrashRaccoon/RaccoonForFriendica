package com.livefast.eattrash.raccoonforfriendica.feature.thread.di

import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.thread.usecase.DefaultPopulateThreadUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.thread.usecase.PopulateThreadUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.singleton

val threadModule =
    DI.Module("ThreadModule") {
        bind<PopulateThreadUseCase> {
            singleton {
                DefaultPopulateThreadUseCase(
                    timelineEntryRepository = instance(),
                    emojiHelper = instance(),
                )
            }
        }
        bind<ThreadMviModel> {
            factory { entryId: String ->
                ThreadViewModel(
                    entryId = entryId,
                    populateThreadUseCase = instance(),
                    timelineEntryRepository = instance(),
                    identityRepository = instance(),
                    settingsRepository = instance(),
                    userRepository = instance(),
                    hapticFeedback = instance(),
                    entryCache = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    imageAutoloadObserver = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
