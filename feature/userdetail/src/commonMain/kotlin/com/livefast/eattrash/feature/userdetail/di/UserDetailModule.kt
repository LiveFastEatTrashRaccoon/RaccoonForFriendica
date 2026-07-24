package com.livefast.eattrash.feature.userdetail.di

import com.livefast.eattrash.feature.userdetail.classic.UserDetailViewModel
import com.livefast.eattrash.feature.userdetail.forum.ForumListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class UserDetailViewModelArgs(val id: String)

data class ForumListViewModelArgs(val id: String)

val userDetailModule = module {
    viewModel { params ->
        val args: UserDetailViewModelArgs = params.get()
        UserDetailViewModel(
            id = args.id,
            userRepository = get(),
            paginationManager = get(),
            timelineEntryRepository = get(),
            identityRepository = get(),
            settingsRepository = get(),
            hapticFeedback = get(),
            userCache = get(),
            imagePreloadManager = get(),
            blurHashRepository = get(),
            accountRepository = get(),
            userRateLimitRepository = get(),
            apiConfigurationRepository = get(),
            instanceShortcutRepository = get(),
            emojiHelper = get(),
            imageAutoloadObserver = get(),
            toggleEntryFavorite = get(),
            toggleEntryDislike = get(),
            getTranslation = get(),
            getInnerUrl = get(),
            timelineNavigationManager = get(),
            notificationCenter = get(),
        )
    }
    viewModel { params ->
        val args: ForumListViewModelArgs = params.get()
        ForumListViewModel(
            id = args.id,
            userRepository = get(),
            paginationManager = get(),
            timelineEntryRepository = get(),
            identityRepository = get(),
            settingsRepository = get(),
            apiConfigurationRepository = get(),
            accountRepository = get(),
            instanceShortcutRepository = get(),
            hapticFeedback = get(),
            userCache = get(),
            imagePreloadManager = get(),
            blurHashRepository = get(),
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
