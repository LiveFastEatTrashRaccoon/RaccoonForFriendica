package com.livefast.eattrash.raccoonforfriendica.feature.calendar.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class EventDetailViewModelArgs(val id: String)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.calendar")
class CalendarModule
