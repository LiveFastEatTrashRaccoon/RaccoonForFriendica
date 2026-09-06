package com.livefast.eattrash.raccoonforfriendica.core.api.di

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.DefaultServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json

@BindingContainer
@ContributesTo(AppScope::class)
object ApiModule {
    @Provides
    @SingleIn(AppScope::class)
    fun json(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @SingleIn(AppScope::class)
    fun httpClientEngine(): HttpClientEngine = provideHttpClientEngine()

    @Provides
    @Named("other")
    @SingleIn(AppScope::class)
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
