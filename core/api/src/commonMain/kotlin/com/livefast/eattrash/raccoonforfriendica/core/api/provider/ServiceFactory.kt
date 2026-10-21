package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import io.ktor.client.HttpClient
import kotlin.reflect.KClass

interface ServiceFactory {
    fun <T : Any> create(clazz: KClass<T>, baseUrl: String, client: HttpClient): T
}
