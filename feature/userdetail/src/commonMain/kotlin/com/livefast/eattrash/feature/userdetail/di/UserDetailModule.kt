package com.livefast.eattrash.feature.userdetail.di

import com.livefast.eattrash.feature.userdetail.classic.UserDetailViewModel
import com.livefast.eattrash.feature.userdetail.forum.ForumListViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import org.kodein.di.DI
import org.kodein.di.instance

data class UserDetailViewModelArgs(val id: String) : ViewModelCreationArgs

data class ForumListViewModelArgs(val id: String) : ViewModelCreationArgs

val userDetailModule =
    DI.Module("UserDetailModule") {
        bindViewModelWithArgs { args: UserDetailViewModelArgs ->
            UserDetailViewModel(
                id = args.id,
                userRepository = instance(),
                paginationManager = instance(),
                timelineEntryRepository = instance(),
                identityRepository = instance(),
                settingsRepository = instance(),
                hapticFeedback = instance(),
                userCache = instance(),
                imagePreloadManager = instance(),
                blurHashRepository = instance(),
                accountRepository = instance(),
                userRateLimitRepository = instance(),
                apiConfigurationRepository = instance(),
                instanceShortcutRepository = instance(),
                emojiHelper = instance(),
                imageAutoloadObserver = instance(),
                toggleEntryFavorite = instance(),
                toggleEntryDislike = instance(),
                getTranslation = instance(),
                getInnerUrl = instance(),
                timelineNavigationManager = instance(),
                notificationCenter = instance(),
            )
        }
        bindViewModelWithArgs { args: ForumListViewModelArgs ->
            ForumListViewModel(
                id = args.id,
                userRepository = instance(),
                paginationManager = instance(),
                timelineEntryRepository = instance(),
                identityRepository = instance(),
                settingsRepository = instance(),
                apiConfigurationRepository = instance(),
                accountRepository = instance(),
                instanceShortcutRepository = instance(),
                hapticFeedback = instance(),
                userCache = instance(),
                imagePreloadManager = instance(),
                blurHashRepository = instance(),
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
