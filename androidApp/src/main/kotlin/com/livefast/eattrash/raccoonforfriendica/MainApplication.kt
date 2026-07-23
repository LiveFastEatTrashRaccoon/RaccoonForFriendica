package com.livefast.eattrash.raccoonforfriendica

import android.app.Application
import com.livefast.eattrash.raccoonforfriendica.di.setupDi
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupDi {
            androidContext(applicationContext)
        }
    }
}
