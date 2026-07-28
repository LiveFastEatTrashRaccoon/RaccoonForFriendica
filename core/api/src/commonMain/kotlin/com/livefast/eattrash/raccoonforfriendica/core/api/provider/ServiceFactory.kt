package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import com.livefast.eattrash.raccoonforfriendica.core.api.di.ServiceCreationArgs
import kotlin.reflect.KClass

internal interface ServiceFactory {
    fun <T : Any> create(clazz: KClass<T>, args: ServiceCreationArgs): T
}
