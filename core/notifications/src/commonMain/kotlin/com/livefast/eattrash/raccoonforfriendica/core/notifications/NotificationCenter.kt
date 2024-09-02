package com.livefast.eattrash.raccoonforfriendica.core.notifications

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.NotificationCenterEvent
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

@Stable
interface NotificationCenter {
    fun send(event: NotificationCenterEvent)

    fun <T : NotificationCenterEvent> subscribe(clazz: KClass<T>): Flow<T>
}
