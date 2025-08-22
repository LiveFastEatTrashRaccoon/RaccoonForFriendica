package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import com.livefast.eattrash.raccoonforfriendica.core.api.di.ServiceCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.api.di.getService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.AnnouncementService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.AppService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DirectMessageService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.EventService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.FollowRequestService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.InnerTranslationService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.InstanceService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.ListService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.MarkerService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.MediaService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.NotificationService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PhotoAlbumService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PhotoService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PollService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PushService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.ReportService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.SearchService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.StatusService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TagsService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TimelineService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TrendsService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.UserService
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.defaultLogger
import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json

internal class DefaultServiceProvider(
    private val factory: HttpClientEngine,
    private val appInfoRepository: AppInfoRepository,
) : ServiceProvider {
    companion object {
        private const val REAM_NAME = "Friendica"
    }

    override var currentNode: String = ""
    private val _events = MutableSharedFlow<ServiceProviderEvent>()
    override val events: Flow<ServiceProviderEvent> = _events.asSharedFlow()

    override lateinit var announcement: AnnouncementService
    override lateinit var app: AppService
    override lateinit var directMessage: DirectMessageService
    override lateinit var event: EventService
    override lateinit var followRequest: FollowRequestService
    override lateinit var instance: InstanceService
    override lateinit var list: ListService
    override lateinit var marker: MarkerService
    override lateinit var media: MediaService
    override lateinit var notification: NotificationService
    override lateinit var photo: PhotoService
    override lateinit var photoAlbum: PhotoAlbumService
    override lateinit var poll: PollService
    override lateinit var push: PushService
    override lateinit var report: ReportService
    override lateinit var search: SearchService
    override lateinit var status: StatusService
    override lateinit var tag: TagsService
    override lateinit var timeline: TimelineService
    override lateinit var translation: InnerTranslationService
    override lateinit var trend: TrendsService
    override lateinit var user: UserService

    private val baseUrl: String get() = "https://$currentNode/api"

    override fun changeNode(value: String) {
        if (currentNode != value) {
            currentNode = value
            reinitialize(null)
        }
    }

    override fun setAuth(credentials: ServiceCredentials?) {
        reinitialize(credentials)
    }

    private fun reinitialize(credentials: ServiceCredentials?) {
        val client =
            HttpClient(factory) {
                defaultRequest {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = currentNode
                    }
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 600_000
                    connectTimeoutMillis = 30_000
                    socketTimeoutMillis = 30_000
                }
                install(HttpRequestRetry) {
                    retryOnServerErrors(maxRetries = 3)
                    exponentialDelay()
                }
                install(Auth) {
                    when (credentials) {
                        is ServiceCredentials.Basic -> {
                            basic {
                                credentials {
                                    BasicAuthCredentials(
                                        username = credentials.user,
                                        password = credentials.pass,
                                    )
                                }
                                realm = REAM_NAME
                                sendWithoutRequest { true }
                            }
                        }

                        is ServiceCredentials.OAuth2 -> {
                            bearer {
                                loadTokens {
                                    BearerTokens(
                                        accessToken = credentials.accessToken,
                                        refreshToken = credentials.refreshToken,
                                    )
                                }
                                sendWithoutRequest { true }
                            }
                        }

                        else -> Unit
                    }
                }
                if (appInfoRepository.appInfo.value?.isDebug == true) {
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

                HttpResponseValidator {
                    handleResponseException { exception, _ ->
                        if (exception is ClientRequestException &&
                            exception.response.status == HttpStatusCode.Unauthorized
                        ) {
                            _events.tryEmit(ServiceProviderEvent.Unauthorized)
                        }
                    }
                }
            }
        val creationArgs = ServiceCreationArgs(baseUrl = baseUrl, client = client)
        announcement = getService(creationArgs)
        app = getService(creationArgs)
        directMessage = getService(creationArgs)
        event = getService(creationArgs)
        followRequest = getService(creationArgs)
        instance = getService(creationArgs)
        list = getService(creationArgs)
        marker = getService(creationArgs)
        media = getService(creationArgs)
        notification = getService(creationArgs)
        photo = getService(creationArgs)
        photoAlbum = getService(creationArgs)
        poll = getService(creationArgs)
        push = getService(creationArgs)
        report = getService(creationArgs)
        search = getService(creationArgs)
        status = getService(creationArgs)
        tag = getService(creationArgs)
        timeline = getService(creationArgs)
        translation = getService(creationArgs)
        trend = getService(creationArgs)
        user = getService(creationArgs)
    }
}
