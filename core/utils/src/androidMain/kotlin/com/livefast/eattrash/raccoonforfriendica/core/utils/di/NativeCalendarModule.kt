package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.CalendarHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.DefaultCalendarHelper
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal actual val nativeCalendarModule =
    DI.Module("NativeCalendarModule") {
        bind<CalendarHelper> {
            singleton {
                DefaultCalendarHelper(context = instance())
            }
        }
    }
