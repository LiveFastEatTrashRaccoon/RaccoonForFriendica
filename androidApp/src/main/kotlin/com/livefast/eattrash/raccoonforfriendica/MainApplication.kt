package com.livefast.eattrash.raccoonforfriendica

import android.app.Application
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class MainApplication :
    Application(),
    MetroApplication {

    lateinit var rootGraph: AndroidRootGraph
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        rootGraph = createGraph<AndroidRootGraph>()
    }

    override val appComponentProviders: MetroAppComponentProviders get() = rootGraph

    companion object {
        lateinit var instance: MainApplication
            private set
    }
}
