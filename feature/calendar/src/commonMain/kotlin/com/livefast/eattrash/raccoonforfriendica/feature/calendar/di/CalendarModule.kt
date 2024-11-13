package com.livefast.eattrash.raccoonforfriendica.feature.calendar.di

import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarViewModel
import org.koin.dsl.module

val featureCalendarModule =
    module {
        factory<CalendarMviModel> {
            CalendarViewModel(
                identityRepository = get(),
                settingsRepository = get(),
                paginationManager = get(),
            )
        }
        factory<EventDetailMviModel> {
            EventDetailViewModel(
                eventId = it[0],
                eventCache = get(),
                settingsRepository = get(),
            )
        }
    }
