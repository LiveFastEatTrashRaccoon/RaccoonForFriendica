package com.livefast.eattrash.raccoonforfriendica.feature.hashtag.di

import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

val hashtagModule =
    DI.Module("HashtagModule") {
        bind<FollowedHashtagsMviModel> {
            provider {
                FollowedHashtagsViewModel(
                    cache = instance(),
                    paginationManager = instance(),
                    tagRepository = instance(),
                    settingsRepository = instance(),
                    hapticFeedback = instance(),
                    notificationCenter = instance(),
                )
            }
        }
        bind<HashtagMviModel> {
            factory { tag: String ->
                HashtagViewModel(
                    tag = tag,
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
    }
