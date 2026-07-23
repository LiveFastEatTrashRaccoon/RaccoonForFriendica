package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di

import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class EntryDetailViewModelArgs(val id: String, val swipeNavigationEnabled: Boolean)

val entryDetailModule = module {
    viewModel { params ->
        val args: EntryDetailViewModelArgs = params.get()
        EntryDetailViewModel(
            id = args.id,
            swipeNavigationEnabled = args.swipeNavigationEnabled,
            timelineEntryRepository = get(),
            identityRepository = get(),
            settingsRepository = get(),
            userRepository = get(),
            blurHashRepository = get(),
            apiConfigurationRepository = get(),
            accountRepository = get(),
            instanceShortcutRepository = get(),
            entryCache = get(),
            hapticFeedback = get(),
            imagePreloadManager = get(),
            emojiHelper = get(),
            replyHelper = get(),
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
