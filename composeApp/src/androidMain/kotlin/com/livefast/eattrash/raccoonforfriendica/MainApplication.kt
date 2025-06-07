package com.livefast.eattrash.raccoonforfriendica

import android.app.Application
import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.di.initDi
import org.kodein.di.bind
import org.kodein.di.provider

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initDi {
            bind<Context> { provider { applicationContext } }
        }
    }
}
