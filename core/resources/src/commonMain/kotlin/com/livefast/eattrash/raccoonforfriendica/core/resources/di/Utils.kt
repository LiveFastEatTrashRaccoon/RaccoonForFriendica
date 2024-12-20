package com.livefast.eattrash.raccoonforfriendica.core.resources.di

import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import org.kodein.di.instance

fun getCoreResources(): CoreResources {
    val res by RootDI.di.instance<CoreResources>()
    return res
}
