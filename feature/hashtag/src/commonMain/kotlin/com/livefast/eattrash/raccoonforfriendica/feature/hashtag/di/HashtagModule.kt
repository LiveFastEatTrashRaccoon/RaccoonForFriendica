package com.livefast.eattrash.raccoonforfriendica.feature.hashtag.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class HashtagViewModelArgs(val tag: String) : ViewModelCreationArgs

val hashtagModule =
    DI.Module("HashtagModule") {
        bindViewModel {
            FollowedHashtagsViewModel(
                cache = instance(),
                paginationManager = instance(),
                tagRepository = instance(),
                settingsRepository = instance(),
                hapticFeedback = instance(),
                notificationCenter = instance(),
            )
        }
        bindViewModelWithArgs { args: HashtagViewModelArgs ->
            HashtagViewModel(
                tag = args.tag,
                paginationManager = instance(),
                timelineEntryRepository = instance(),
                tagRepository = instance(),
                settingsRepository = instance(),
                identityRepository = instance(),
                userRepository = instance(),
                apiConfigurationRepository = instance(),
                accountRepository = instance(),
                instanceShortcutRepository = instance(),
                hapticFeedback = instance(),
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
