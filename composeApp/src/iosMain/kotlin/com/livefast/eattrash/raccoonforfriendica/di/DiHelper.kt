package com.livefast.eattrash.raccoonforfriendica.di

import org.koin.core.Koin
import org.koin.core.context.startKoin

fun initKoin(): Koin {
    val koinApp =
        startKoin {
            modules(rootModule)
        }
    return koinApp.koin
}
