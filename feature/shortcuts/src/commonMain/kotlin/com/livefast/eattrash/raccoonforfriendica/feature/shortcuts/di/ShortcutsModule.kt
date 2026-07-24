package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.di

import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline.ShortcutTimelineViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class ShortcutTimelineViewModelArgs(val name: String)

val shortcutsModule = module {
    viewModel {
        ShortcutListViewModel(
            shortcutRepository = get(),
            accountRepository = get(),
            settingsRepository = get(),
        )
    }
    viewModel { params ->
        val args: ShortcutTimelineViewModelArgs = params.get()
        ShortcutTimelineViewModel(
            node = args.name,
            paginationManager = get(),
            identityRepository = get(),
            timelineEntryRepository = get(),
            settingsRepository = get(),
            searchRepository = get(),
            hapticFeedback = get(),
            imagePreloadManager = get(),
            blurHashRepository = get(),
            imageAutoloadObserver = get(),
            toggleEntryDislike = get(),
            toggleEntryFavorite = get(),
            getTranslation = get(),
            getInnerUrl = get(),
            timelineNavigationManager = get(),
            notificationCenter = get(),
        )
    }
}
