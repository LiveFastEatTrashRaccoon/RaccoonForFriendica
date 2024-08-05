package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import com.livefast.eattrash.raccoonforfriendica.core.api.service.TimelineService
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class DefaultServiceProvider(
    private val factory: HttpClientEngine = provideHttpClientEngine(),
) : ServiceProvider {
    companion object {
        private const val VERSION = "v1"
        private const val ENABLE_LOGGING = false
    }

    private var currentNode: String = ""

    override lateinit var timeline: TimelineService

    private val baseUrl: String get() = "https://$currentNode/api/$VERSION/"

    override fun changeNode(value: String) {
        if (currentNode != value) {
            currentNode = value
            reinitialize()
        }
    }

    private fun reinitialize() {
        val client =
            HttpClient(factory) {
                defaultRequest {
                    url {
                        host = currentNode
                    }
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 600_000
                    connectTimeoutMillis = 30_000
                    socketTimeoutMillis = 30_000
                }
                if (ENABLE_LOGGING) {
                    install(Logging) {
                        logger = defaultLogger
                        level = LogLevel.ALL
                    }
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                        },
                    )
                }
            }
        val ktorfit =
            Ktorfit
                .Builder()
                .baseUrl(baseUrl)
                .httpClient(client)
                .build()
        timeline = ktorfit.create()
    }
}
