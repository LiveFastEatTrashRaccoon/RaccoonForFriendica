package com.livefast.eattrash.raccoonforfriendica

import dev.zacsweers.metro.createGraph

object IosGraphHolder {
    val rootGraph: IosRootGraph by lazy { createGraph<IosRootGraph>() }
}
