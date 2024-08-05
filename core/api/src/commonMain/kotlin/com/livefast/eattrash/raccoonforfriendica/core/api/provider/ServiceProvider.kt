package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import com.livefast.eattrash.raccoonforfriendica.core.api.service.TimelineService

interface ServiceProvider {
    val timeline: TimelineService

    fun changeNode(value: String)
}
