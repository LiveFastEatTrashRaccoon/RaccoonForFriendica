package com.livefast.eattrash.raccoonforfriendica.core.resources.di

import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import com.livefast.eattrash.raccoonforfriendica.core.resources.DefaultCoreResources
import org.kodein.di.DI
import org.kodein.di.bindSingleton

val resourcesModule = DI.Module("ResourcesModule") {
    bindSingleton<CoreResources> { DefaultCoreResources() }
}
