package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import io.ktor.client.HttpClient
import kotlin.reflect.KClass

data class ServiceCreationArgs(val baseUrl: String, val client: HttpClient)

interface ServiceFactory {
    fun <T : Any> create(clazz: KClass<T>, args: ServiceCreationArgs): T
}
