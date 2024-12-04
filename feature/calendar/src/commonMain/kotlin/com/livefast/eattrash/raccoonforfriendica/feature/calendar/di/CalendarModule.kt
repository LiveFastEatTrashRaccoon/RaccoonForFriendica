package com.livefast.eattrash.raccoonforfriendica.feature.calendar.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.calendar")
internal class CalendarModule

val featureCalendarModule = CalendarModule().module
