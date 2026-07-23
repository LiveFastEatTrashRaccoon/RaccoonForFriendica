package com.livefast.eattrash.raccoonforfriendica.core.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

actual fun <T : Any> getByInjection(clazz: KClass<T>, qualifier: Qualifier?, parameters: ParametersDefinition?): T =
    InnerHelper.retrieve(qualifier = qualifier, parameters = parameters)

private object InnerHelper : KoinComponent {
    fun <T> retrieve(qualifier: Qualifier?, parameters: ParametersDefinition?): T {
        val res: T by inject(qualifier = qualifier, parameters = parameters)
        return res
    }
}
