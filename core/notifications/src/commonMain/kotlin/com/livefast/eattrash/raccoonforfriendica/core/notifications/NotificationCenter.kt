package com.livefast.eattrash.raccoonforfriendica.core.notifications

import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.NotificationCenterEvent
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface NotificationCenter {
    fun send(event: NotificationCenterEvent)

    fun <T : NotificationCenterEvent> subscribe(clazz: KClass<T>): Flow<T>
}
