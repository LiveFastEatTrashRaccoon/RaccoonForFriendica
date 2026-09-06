package com.livefast.eattrash.raccoonforfriendica

import androidx.compose.ui.window.ComposeUIViewController
import dev.zacsweers.metro.createGraph

val iosRootGraph: IosRootGraph by lazy { createGraph<IosRootGraph>() }

fun MainViewController() = ComposeUIViewController {
    App(graph = iosRootGraph)
}
