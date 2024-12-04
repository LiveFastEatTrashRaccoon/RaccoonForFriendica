package com.livefast.eattrash.raccoonforfriendica.feature.hashtag.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.hashtag")
internal class HashtagModule

val featureHashtagModule = HashtagModule().module
