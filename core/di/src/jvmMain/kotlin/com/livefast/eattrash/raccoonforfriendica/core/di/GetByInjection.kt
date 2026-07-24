package com.livefast.eattrash.raccoonforfriendica.core.di

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.java.KoinJavaComponent.inject
import kotlin.reflect.KClass

actual fun <T : Any> getByInjection(clazz: KClass<T>, qualifier: Qualifier?, parameters: ParametersDefinition?): T {
    val res: T by inject(clazz = clazz.java, qualifier = qualifier, parameters = parameters)
    return res
}
