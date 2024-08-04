package com.github.akesiseli.raccoonforfriendica.core.api.provider

import com.github.akesiseli.raccoonforfriendica.core.api.service.TimelineService

interface ServiceProvider {
    val timeline: TimelineService

    fun changeNode(value: String)
}
