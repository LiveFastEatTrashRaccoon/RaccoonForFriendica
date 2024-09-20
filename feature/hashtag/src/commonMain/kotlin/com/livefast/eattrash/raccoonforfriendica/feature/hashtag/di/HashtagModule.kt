package com.livefast.eattrash.raccoonforfriendica.feature.hashtag.di

import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagViewModel
import org.koin.dsl.module

val featureHashtagModule =
    module {
        factory<HashtagMviModel> { params ->
            HashtagViewModel(
                tag = params[0],
                paginationManager = get(),
                timelineEntryRepository = get(),
                tagRepository = get(),
                settingsRepository = get(),
                identityRepository = get(),
                userRepository = get(),
                hapticFeedback = get(),
                notificationCenter = get(),
                imagePreloadManager = get(),
                blurHashRepository = get(),
            )
        }
        factory<FollowedHashtagsMviModel> {
            FollowedHashtagsViewModel(
                paginationManager = get(),
                tagRepository = get(),
                hapticFeedback = get(),
                notificationCenter = get(),
            )
        }
    }
