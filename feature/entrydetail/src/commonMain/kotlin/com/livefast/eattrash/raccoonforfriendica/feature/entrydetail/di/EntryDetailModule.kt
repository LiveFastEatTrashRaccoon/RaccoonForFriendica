package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class EntryDetailViewModelArgs(val id: String, val swipeNavigationEnabled: Boolean) : ViewModelCreationArgs

val entryDetailModule =
    DI.Module("EntryDetailModule") {
        bindViewModelWithArgs { args: EntryDetailViewModelArgs ->
            EntryDetailViewModel(
                id = args.id,
                swipeNavigationEnabled = args.swipeNavigationEnabled,
                timelineEntryRepository = instance(),
                identityRepository = instance(),
                settingsRepository = instance(),
                userRepository = instance(),
                blurHashRepository = instance(),
                apiConfigurationRepository = instance(),
                accountRepository = instance(),
                instanceShortcutRepository = instance(),
                entryCache = instance(),
                hapticFeedback = instance(),
                imagePreloadManager = instance(),
                emojiHelper = instance(),
                replyHelper = instance(),
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
