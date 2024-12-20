package com.livefast.eattrash.raccoonforfriendica

import android.app.Application
import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.di.initDi
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.provider

class MainApplication :
    Application(),
    DIAware {
    override val di: DI get() = RootDI.di

    override fun onCreate() {
        super.onCreate()
        initDi {
            bind<Context> { provider { applicationContext } }
        }
    }
}
