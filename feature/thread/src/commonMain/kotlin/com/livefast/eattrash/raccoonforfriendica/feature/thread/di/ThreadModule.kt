package com.livefast.eattrash.raccoonforfriendica.feature.thread.di

import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class ThreadViewModelArgs(val entryId: String, val swipeNavigationEnabled: Boolean)

val threadModule = module {
    viewModel { params ->
        val args: ThreadViewModelArgs = params.get()
        ThreadViewModel(
            entryId = args.entryId,
            swipeNavigationEnabled = args.swipeNavigationEnabled,
            timelineEntryRepository = get(),
            identityRepository = get(),
            settingsRepository = get(),
            userRepository = get(),
            apiConfigurationRepository = get(),
            accountRepository = get(),
            instanceShortcutRepository = get(),
            hapticFeedback = get(),
            entryCache = get(),
            imagePreloadManager = get(),
            blurHashRepository = get(),
            imageAutoloadObserver = get(),
            populateThread = get(),
            toggleEntryFavorite = get(),
            toggleEntryDislike = get(),
            getTranslation = get(),
            getInnerUrl = get(),
            timelineNavigationManager = get(),
            notificationCenter = get(),
        )
    }
}
