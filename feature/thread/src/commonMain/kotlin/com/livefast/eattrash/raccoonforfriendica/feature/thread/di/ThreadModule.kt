package com.livefast.eattrash.raccoonforfriendica.feature.thread.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class ThreadViewModelArgs(val entryId: String, val swipeNavigationEnabled: Boolean) : ViewModelCreationArgs

val threadModule =
    DI.Module("ThreadModule") {
        bindViewModelWithArgs { args: ThreadViewModelArgs ->
            ThreadViewModel(
                entryId = args.entryId,
                swipeNavigationEnabled = args.swipeNavigationEnabled,
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
                populateThread = instance(),
                toggleEntryFavorite = instance(),
                toggleEntryDislike = instance(),
                getTranslation = instance(),
                getInnerUrl = instance(),
                timelineNavigationManager = instance(),
                notificationCenter = instance(),
            )
        }
    }
