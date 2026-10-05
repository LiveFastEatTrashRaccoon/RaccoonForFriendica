package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common

import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.LogFactory

interface PushNotificationComponent {
    val unifiedPushInteractor: UnifiedPushInteractor

    val logFactory: LogFactory
}
