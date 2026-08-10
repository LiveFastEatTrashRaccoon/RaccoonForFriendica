package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import kotlin.reflect.KClass

@Single
internal class DefaultServiceFactory :
    ServiceFactory,
    KoinComponent {
    override fun <T : Any> create(clazz: KClass<T>, args: ServiceCreationArgs): T =
        getKoin().get(clazz = clazz, qualifier = null, parameters = { parametersOf(args) })
}
