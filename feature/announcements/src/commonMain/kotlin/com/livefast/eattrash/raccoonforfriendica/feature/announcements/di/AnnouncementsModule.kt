package com.livefast.eattrash.raccoonforfriendica.feature.announcements.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.announcements")
internal class AnnouncementsModule

val featureAnnouncementsModule = AnnouncementsModule().module
