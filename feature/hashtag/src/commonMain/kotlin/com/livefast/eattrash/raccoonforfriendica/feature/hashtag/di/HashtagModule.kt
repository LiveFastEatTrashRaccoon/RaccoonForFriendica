package com.livefast.eattrash.raccoonforfriendica.feature.hashtag.di

import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.HashtagMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.HashtagViewModel
import org.koin.dsl.module

val featureHashtagModule =
    module {
        factory<HashtagMviModel> { params ->
            HashtagViewModel(
                tag = params[0],
                paginationManager = get(),
            )
        }
    }
