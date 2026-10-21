package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Application
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateAppForm
import io.ktor.client.HttpClient

interface AppService {
    suspend fun create(data: CreateAppForm): Application

    suspend fun verifyCredentials(): Application
}

fun interface AppServiceFactory {
    fun create(baseUrl: String, client: HttpClient): AppService
}
