package com.livefast.eattrash.raccoonforfriendica.feature.timeline.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.timeline")
internal class TimelineModule

val featureTimelineModule = TimelineModule().module
