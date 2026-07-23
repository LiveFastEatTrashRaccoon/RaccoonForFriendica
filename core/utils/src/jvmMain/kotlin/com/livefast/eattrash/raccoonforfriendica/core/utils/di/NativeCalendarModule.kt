package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.CalendarHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.DefaultCalendarHelper
import org.koin.dsl.module

internal actual val nativeCalendarModule = module {
    single<CalendarHelper> {
        DefaultCalendarHelper()
    }
}
