package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import com.livefast.eattrash.raccoonforfriendica.core.api.service.AccountService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.NotificationService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.StatusService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TimelineService

interface ServiceProvider {
    val accounts: AccountService
    val notifications: NotificationService
    val statuses: StatusService
    val timeline: TimelineService

    fun changeNode(value: String)

    fun setAuth(credentials: Pair<String, String>? = null)
}
