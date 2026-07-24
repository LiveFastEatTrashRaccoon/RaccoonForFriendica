package com.livefast.eattrash.raccoonforfriendica.feature.calendar.di

import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class EventDetailViewModelArgs(val id: String)

val calendarModule = module {
    viewModel {
        CalendarViewModel(
            identityRepository = get(),
            settingsRepository = get(),
            paginationManager = get(),
        )
    }
    viewModel { params ->
        val args: EventDetailViewModelArgs = params.get()
        EventDetailViewModel(
            eventId = args.id,
            eventCache = get(),
            settingsRepository = get(),
        )
    }
}
