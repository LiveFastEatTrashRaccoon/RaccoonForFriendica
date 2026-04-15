package com.livefast.eattrash.raccoonforfriendica.feature.entrylist.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EntryListType
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.EntryListViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class EntryListViewModelArgs(val type: EntryListType) : ViewModelCreationArgs

val entryListModule =
    DI.Module("FavoritesModule") {
        bindViewModelWithArgs { args: EntryListViewModelArgs ->
            EntryListViewModel(
                type = args.type,
                paginationManager = instance(),
                timelineEntryRepository = instance(),
                settingsRepository = instance(),
                identityRepository = instance(),
                userRepository = instance(),
                apiConfigurationRepository = instance(),
                accountRepository = instance(),
                instanceShortcutRepository = instance(),
                blurHashRepository = instance(),
                hapticFeedback = instance(),
                imagePreloadManager = instance(),
                imageAutoloadObserver = instance(),
                toggleEntryFavorite = instance(),
                toggleEntryDislike = instance(),
                getTranslation = instance(),
                getInnerUrl = instance(),
                timelineNavigationManager = instance(),
                notificationCenter = instance(),
            )
        }
    }
