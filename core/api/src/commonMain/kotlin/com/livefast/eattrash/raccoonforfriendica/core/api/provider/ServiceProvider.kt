package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import com.livefast.eattrash.raccoonforfriendica.core.api.service.AccountService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TimelineService

interface ServiceProvider {
    val timeline: TimelineService
    val account: AccountService

    fun changeNode(value: String)
}
