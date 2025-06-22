package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline.ShortcutTimelineViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class ShortcutTimelineViewModelArgs(val name: String) : ViewModelCreationArgs

val shortcutsModule =
    DI.Module("ShortcutsModule") {
        bindViewModel {
            ShortcutListViewModel(
                shortcutRepository = instance(),
                accountRepository = instance(),
                settingsRepository = instance(),
            )
        }
        bindViewModelWithArgs { args: ShortcutTimelineViewModelArgs ->
            ShortcutTimelineViewModel(
                node = args.name,
                paginationManager = instance(),
                identityRepository = instance(),
                timelineEntryRepository = instance(),
                settingsRepository = instance(),
                searchRepository = instance(),
                hapticFeedback = instance(),
                imagePreloadManager = instance(),
                blurHashRepository = instance(),
                imageAutoloadObserver = instance(),
                toggleEntryDislike = instance(),
                toggleEntryFavorite = instance(),
                getTranslation = instance(),
                getInnerUrl = instance(),
                timelineNavigationManager = instance(),
                notificationCenter = instance(),
            )
        }
    }
