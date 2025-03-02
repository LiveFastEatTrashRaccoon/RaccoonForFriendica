package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.di

import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline.ShortcutTimelineMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline.ShortcutTimelineViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

val shortcutsModule =
    DI.Module("ShortcutsModule") {
        bind<ShortcutListMviModel> {
            provider {
                ShortcutListViewModel(
                    shortcutRepository = instance(),
                    accountRepository = instance(),
                    settingsRepository = instance(),
                )
            }
        }
        bind<ShortcutTimelineMviModel> {
            factory { name: String ->
                ShortcutTimelineViewModel(
                    node = name,
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
    }
