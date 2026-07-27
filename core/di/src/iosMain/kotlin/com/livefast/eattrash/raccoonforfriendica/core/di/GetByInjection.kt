package com.livefast.eattrash.raccoonforfriendica.core.di

import org.koin.core.component.KoinComponent
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

@DelicateDiApi
actual fun <T : Any> getByInjection(clazz: KClass<T>, qualifier: Qualifier?, parameters: ParametersDefinition?): T =
    InnerHelper.getKoin().get(clazz = clazz, qualifier = qualifier, parameters = parameters)

private object InnerHelper : KoinComponent
