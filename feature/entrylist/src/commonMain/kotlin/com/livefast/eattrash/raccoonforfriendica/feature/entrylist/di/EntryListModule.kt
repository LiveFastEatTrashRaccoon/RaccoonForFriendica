package com.livefast.eattrash.raccoonforfriendica.feature.entrylist.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EntryListType
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.EntryListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class EntryListViewModelArgs(val type: EntryListType)

val entryListModule = module {
    viewModel { params ->
        val args: EntryListViewModelArgs = params.get()
        EntryListViewModel(
            type = args.type,
            paginationManager = get(),
            timelineEntryRepository = get(),
            settingsRepository = get(),
            identityRepository = get(),
            userRepository = get(),
            apiConfigurationRepository = get(),
            accountRepository = get(),
            instanceShortcutRepository = get(),
            blurHashRepository = get(),
            hapticFeedback = get(),
            imagePreloadManager = get(),
            imageAutoloadObserver = get(),
            toggleEntryFavorite = get(),
            toggleEntryDislike = get(),
            getTranslation = get(),
            getInnerUrl = get(),
            timelineNavigationManager = get(),
            notificationCenter = get(),
        )
    }
}
