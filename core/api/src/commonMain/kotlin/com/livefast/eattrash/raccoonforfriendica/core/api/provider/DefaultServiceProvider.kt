package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import com.livefast.eattrash.raccoonforfriendica.core.api.service.AppService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DirectMessageService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.EventService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.FollowRequestService
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
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createAppService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createDirectMessageService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createEventService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createFollowRequestService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createInstanceService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createListService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createMarkerService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createMediaService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createNotificationService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createPhotoAlbumService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createPhotoService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createPollService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createPushService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createReportService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createSearchService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createStatusService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createTagsService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createTimelineService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createTrendsService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.createUserService
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.defaultLogger
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.ResponseConverterFactory
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
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class DefaultServiceProvider(
    private val factory: HttpClientEngine = provideHttpClientEngine(),
    private val appInfoRepository: AppInfoRepository,
) : ServiceProvider {
    companion object {
        private const val REAM_NAME = "Friendica"
    }

    private var currentNode: String = ""

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
    override lateinit var trends: TrendsService
    override lateinit var users: UserService

    private val baseUrl: String get() = "https://$currentNode/api/"

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
        val ktorfit =
            Ktorfit
                .Builder()
                .baseUrl(baseUrl)
                .httpClient(client)
                .converterFactories(ResponseConverterFactory())
                .build()
        apps = ktorfit.createAppService()
        directMessage = ktorfit.createDirectMessageService()
        events = ktorfit.createEventService()
        followRequests = ktorfit.createFollowRequestService()
        instance = ktorfit.createInstanceService()
        lists = ktorfit.createListService()
        markers = ktorfit.createMarkerService()
        media = ktorfit.createMediaService()
        notifications = ktorfit.createNotificationService()
        photo = ktorfit.createPhotoService()
        photoAlbum = ktorfit.createPhotoAlbumService()
        polls = ktorfit.createPollService()
        push = ktorfit.createPushService()
        reports = ktorfit.createReportService()
        search = ktorfit.createSearchService()
        statuses = ktorfit.createStatusService()
        tags = ktorfit.createTagsService()
        timeline = ktorfit.createTimelineService()
        trends = ktorfit.createTrendsService()
        users = ktorfit.createUserService()
    }
}
