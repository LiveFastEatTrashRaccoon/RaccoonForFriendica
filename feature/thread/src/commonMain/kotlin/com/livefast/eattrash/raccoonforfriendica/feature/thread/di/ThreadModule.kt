package com.livefast.eattrash.raccoonforfriendica.feature.thread.di

import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

data class ThreadMviModelParams(
    val entryId: String,
    val swipeNavigationEnabled: Boolean,
)

val threadModule =
    DI.Module("ThreadModule") {
        bind<ThreadMviModel> {
            factory { params: ThreadMviModelParams ->
                ThreadViewModel(
                    entryId = params.entryId,
                    swipeNavigationEnabled = params.swipeNavigationEnabled,
                    populateThreadUseCase = instance(),
                    timelineEntryRepository = instance(),
                    identityRepository = instance(),
                    settingsRepository = instance(),
                    userRepository = instance(),
                    apiConfigurationRepository = instance(),
                    accountRepository = instance(),
                    instanceShortcutRepository = instance(),
                    hapticFeedback = instance(),
                    entryCache = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    imageAutoloadObserver = instance(),
                    toggleEntryFavorite = instance(),
                    toggleEntryDislike = instance(),
                    getTranslation = instance(),
                    timelineNavigationManager = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
