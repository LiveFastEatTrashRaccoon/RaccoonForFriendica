package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Application
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateAppForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted

interface AppService {
    suspend fun create(data: CreateAppForm): Application

    suspend fun verifyCredentials(): Application
}

fun interface AppServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): AppService
}
