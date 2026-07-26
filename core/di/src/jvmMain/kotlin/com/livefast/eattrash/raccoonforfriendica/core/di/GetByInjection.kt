package com.livefast.eattrash.raccoonforfriendica.core.di

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.java.KoinJavaComponent.get
import kotlin.reflect.KClass

@DelicateDiApi
actual fun <T : Any> getByInjection(clazz: KClass<T>, qualifier: Qualifier?, parameters: ParametersDefinition?): T =
    get(clazz = clazz.java, qualifier = qualifier, parameters = parameters)
