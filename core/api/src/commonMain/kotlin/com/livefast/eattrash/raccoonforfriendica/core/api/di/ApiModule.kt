package com.livefast.eattrash.raccoonforfriendica.core.api.di

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.DefaultServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import io.ktor.client.engine.HttpClientEngine
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val apiModule =
    DI.Module("ApiModule") {
        bind<HttpClientEngine> {
            instance(provideHttpClientEngine())
        }
        bind<ServiceProvider>(tag = "default") {
            singleton {
                DefaultServiceProvider(
                    appInfoRepository = instance(),
                )
            }
        }
        bind<ServiceProvider>(tag = "other") {
            singleton {
                DefaultServiceProvider(
                    appInfoRepository = instance(),
                )
            }
        }
    }
