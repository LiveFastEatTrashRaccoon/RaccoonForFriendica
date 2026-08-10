package com.livefast.eattrash.raccoonforfriendica.feature.hashtag.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class HashtagViewModelArgs(val tag: String)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.hashtag")
class HashtagModule
