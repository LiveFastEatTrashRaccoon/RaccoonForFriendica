package com.livefast.eattrash.raccoonforfriendica.core.api.di

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.DefaultServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.api")
class ApiModule {

    @Single
    fun json(): Json = Json { ignoreUnknownKeys = true }

    @Single
    fun httpClientEngine(): HttpClientEngine = provideHttpClientEngine()

    @Factory
    @Named("other")
    fun otherServiceProvider(
        engine: HttpClientEngine,
        appInfoRepository: AppInfoRepository,
        factory: ServiceFactory,
    ): ServiceProvider = DefaultServiceProvider(
        engine = engine,
        appInfoRepository = appInfoRepository,
        factory = factory,
    )
}
