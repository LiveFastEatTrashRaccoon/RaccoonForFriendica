package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Application
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateAppForm

interface AppService {
    suspend fun create(data: CreateAppForm): Application

    suspend fun verifyCredentials(): Application
}
