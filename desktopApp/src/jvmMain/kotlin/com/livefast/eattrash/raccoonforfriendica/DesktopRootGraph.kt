package com.livefast.eattrash.raccoonforfriendica

import com.livefast.eattrash.raccoonforfriendica.di.RootGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(scope = AppScope::class)
interface DesktopRootGraph : RootGraph
