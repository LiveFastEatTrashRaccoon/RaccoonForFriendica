package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import com.livefast.eattrash.raccoonforfriendica.core.api.service.NotificationService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.StatusService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TagsService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TimelineService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TrendsService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.UserService
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
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
        private const val ENABLE_LOGGING = false
        private const val REAM_NAME = "Friendica"
    }

    private var currentNode: String = ""

    override lateinit var notifications: NotificationService
    override lateinit var statuses: StatusService
    override lateinit var tags: TagsService
    override lateinit var timeline: TimelineService
    override lateinit var trends: TrendsService
    override lateinit var users: UserService

    private val baseUrl: String get() = "https://$currentNode/api/"

    override fun changeNode(value: String) {
        if (currentNode != value) {
            currentNode = value
            reinitialize(null)
        }
    }

    override fun setAuth(credentials: Pair<String, String>?) {
        val basicAuthCredentials =
            if (credentials == null) {
                null
            } else {
                BasicAuthCredentials(
                    username = credentials.first,
                    password = credentials.second,
                )
            }
        reinitialize(basicAuthCredentials)
    }

    private fun reinitialize(basicCredentials: BasicAuthCredentials?) {
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
                install(Auth) {
                    basic {
                        credentials { basicCredentials }
                        realm = REAM_NAME
                        sendWithoutRequest { true }
                    }
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
        notifications = ktorfit.create()
        statuses = ktorfit.create()
        tags = ktorfit.create()
        timeline = ktorfit.create()
        trends = ktorfit.create()
        users = ktorfit.create()
    }
}
