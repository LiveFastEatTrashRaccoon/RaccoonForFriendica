package com.livefast.eattrash.raccoonforfriendica.core.api.di

import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import org.koin.core.parameter.parametersOf

internal inline fun <reified T : Any> getService(args: ServiceCreationArgs): T {
    val res: T = getByInjection(clazz = T::class, parameters = { parametersOf(args) })
    return res
}
