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
                    hapticFeedback = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    imageAutoloadObserver = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
