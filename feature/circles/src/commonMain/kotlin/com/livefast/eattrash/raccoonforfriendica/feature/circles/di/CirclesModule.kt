package com.livefast.eattrash.raccoonforfriendica.feature.circles.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class CircleMembersViewModelArgs(val id: String)

data class CircleTimelineViewModelArgs(val id: String)

data class ManageUserCirclesViewModelArgs(val userId: String)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.circles")
class CirclesModule
