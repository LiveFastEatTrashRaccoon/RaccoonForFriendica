package com.livefast.eattrash.raccoonforfriendica.core.resources.di

import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources

fun getCoreResources(): CoreResources = getByInjection(CoreResources::class)
