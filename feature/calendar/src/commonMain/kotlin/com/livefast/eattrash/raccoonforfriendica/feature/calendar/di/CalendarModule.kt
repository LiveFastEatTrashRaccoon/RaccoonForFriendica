package com.livefast.eattrash.raccoonforfriendica.feature.calendar.di

import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

val calendarModule =
    DI.Module("CalendarModule") {
        bind<CalendarMviModel> {
            provider {
                CalendarViewModel(
                    identityRepository = instance(),
                    settingsRepository = instance(),
                    paginationManager = instance(),
                )
            }
        }
        bind<EventDetailMviModel> {
            factory { id: String ->
                EventDetailViewModel(
                    eventId = id,
                    eventCache = instance(),
                    settingsRepository = instance(),
                )
            }
        }
    }
