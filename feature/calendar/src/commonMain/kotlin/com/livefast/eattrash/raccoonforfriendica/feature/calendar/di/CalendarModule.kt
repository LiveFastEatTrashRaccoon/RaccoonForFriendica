package com.livefast.eattrash.raccoonforfriendica.feature.calendar.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class EventDetailViewModelArgs(val id: String) : ViewModelCreationArgs

val calendarModule =
    DI.Module("CalendarModule") {
        bindViewModel {
            CalendarViewModel(
                identityRepository = instance(),
                settingsRepository = instance(),
                paginationManager = instance(),
            )
        }
        bindViewModelWithArgs { args: EventDetailViewModelArgs ->
            EventDetailViewModel(
                eventId = args.id,
                eventCache = instance(),
                settingsRepository = instance(),
            )
        }
    }
