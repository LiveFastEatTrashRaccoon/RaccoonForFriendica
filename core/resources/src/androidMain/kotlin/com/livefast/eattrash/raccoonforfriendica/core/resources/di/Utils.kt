package com.livefast.eattrash.raccoonforfriendica.core.resources.di

import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import org.koin.java.KoinJavaComponent

actual fun getCoreResources(): CoreResources {
    val res by KoinJavaComponent.inject<CoreResources>(CoreResources::class.java)
    return res
}
