package com.livefast.eattrash.raccoonforfriendica.feature.hashtag.di

import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class HashtagViewModelArgs(val tag: String)

val hashtagModule = module {
    viewModel {
        FollowedHashtagsViewModel(
            cache = get(),
            paginationManager = get(),
            tagRepository = get(),
            settingsRepository = get(),
            hapticFeedback = get(),
            notificationCenter = get(),
        )
    }
    viewModel { params ->
        val args: HashtagViewModelArgs = params.get()
        HashtagViewModel(
            tag = args.tag,
            paginationManager = get(),
            timelineEntryRepository = get(),
            tagRepository = get(),
            settingsRepository = get(),
            identityRepository = get(),
            userRepository = get(),
            apiConfigurationRepository = get(),
            accountRepository = get(),
            instanceShortcutRepository = get(),
            hapticFeedback = get(),
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
