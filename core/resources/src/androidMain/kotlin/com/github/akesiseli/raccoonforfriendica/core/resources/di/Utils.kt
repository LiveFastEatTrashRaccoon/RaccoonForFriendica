package com.github.akesiseli.raccoonforfriendica.core.resources.di

import com.github.akesiseli.raccoonforfriendica.core.resources.CoreResources
import org.koin.java.KoinJavaComponent

actual fun getCoreResources(): CoreResources {
    val res by KoinJavaComponent.inject<CoreResources>(CoreResources::class.java)
    return res
}
