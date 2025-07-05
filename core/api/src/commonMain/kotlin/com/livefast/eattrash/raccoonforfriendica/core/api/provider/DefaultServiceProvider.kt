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
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class DefaultServiceProvider(
    private val factory: HttpClientEngine,
    private val appInfoRepository: AppInfoRepository,
) : ServiceProvider {
    companion object {
        private const val REAM_NAME = "Friendica"
    }

    override var currentNode: String = ""

    override lateinit var announcements: AnnouncementService
    override lateinit var apps: AppService
    override lateinit var directMessage: DirectMessageService
    override lateinit var events: EventService
    override lateinit var followRequests: FollowRequestService
    override lateinit var instance: InstanceService
    override lateinit var lists: ListService
    override lateinit var markers: MarkerService
    override lateinit var media: MediaService
    override lateinit var notifications: NotificationService
    override lateinit var photo: PhotoService
    override lateinit var photoAlbum: PhotoAlbumService
    override lateinit var polls: PollService
    override lateinit var push: PushService
    override lateinit var reports: ReportService
    override lateinit var search: SearchService
    override lateinit var statuses: StatusService
    override lateinit var tags: TagsService
    override lateinit var timeline: TimelineService
    override lateinit var translationService: InnerTranslationService
    override lateinit var trends: TrendsService
    override lateinit var users: UserService

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
            }
        val creationArgs = ServiceCreationArgs(baseUrl = baseUrl, client = client)
        announcements = getService(creationArgs)
        apps = getService(creationArgs)
        directMessage = getService(creationArgs)
        events = getService(creationArgs)
        followRequests = getService(creationArgs)
        instance = getService(creationArgs)
        lists = getService(creationArgs)
        markers = getService(creationArgs)
        media = getService(creationArgs)
        notifications = getService(creationArgs)
        photo = getService(creationArgs)
        photoAlbum = getService(creationArgs)
        polls = getService(creationArgs)
        push = getService(creationArgs)
        reports = getService(creationArgs)
        search = getService(creationArgs)
        statuses = getService(creationArgs)
        tags = getService(creationArgs)
        timeline = getService(creationArgs)
        translationService = getService(creationArgs)
        trends = getService(creationArgs)
        users = getService(creationArgs)
    }
}
