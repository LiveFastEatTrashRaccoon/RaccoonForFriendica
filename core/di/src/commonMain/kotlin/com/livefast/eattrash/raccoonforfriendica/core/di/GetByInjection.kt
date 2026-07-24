package com.livefast.eattrash.raccoonforfriendica.core.di

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

expect fun <T : Any> getByInjection(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null,
): T
