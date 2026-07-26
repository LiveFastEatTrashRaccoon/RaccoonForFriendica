package com.livefast.eattrash.raccoonforfriendica.core.di

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * Marks delicate DI APIs that should be used with caution (only in specific scenarios where standard constructor
 * injection is not possible, e.g. in Compose `remember` blocks to provide dependencies to the UI layer).
 *
 * @see getByInjection documentation for details
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This API is delicate and should be used with caution. " +
        "It is intended for use in controlled environments, such as within Compose 'remember' blocks " +
        "to expose UI-related dependencies to the UI layer.",
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class DelicateDiApi

/**
 * Resolves a dependency from the DI container manually.
 *
 * This function should be used with caution and only in specific scenarios where standard constructor injection is not
 * possible (e.g. in Compose `remember` blocks to provide dependencies to the UI layer).
 *
 * @param clazz [KClass] of the dependency to resolve
 * @param qualifier DI [Qualifier] (optional)
 * @param parameters DI factory [ParametersDefinition] (optional)
 * @return resolved dependency of type [T]
 */
@DelicateDiApi
expect fun <T : Any> getByInjection(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null,
): T
