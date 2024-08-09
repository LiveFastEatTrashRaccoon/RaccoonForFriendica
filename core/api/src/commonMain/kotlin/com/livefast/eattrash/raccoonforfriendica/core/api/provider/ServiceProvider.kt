package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import com.livefast.eattrash.raccoonforfriendica.core.api.service.AccountService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.NotificationService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.StatusService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TimelineService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TrendsService

interface ServiceProvider {
    val accounts: AccountService
    val notifications: NotificationService
    val statuses: StatusService
    val timeline: TimelineService
    val trends: TrendsService

    fun changeNode(value: String)

    fun setAuth(credentials: Pair<String, String>? = null)
}
