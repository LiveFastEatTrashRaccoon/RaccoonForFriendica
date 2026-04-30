package com.livefast.eattrash.raccoonforfriendica.core.api.di

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.DefaultServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.AnnouncementService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.AppService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultAnnouncementService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultAppService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultDirectMessageService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultEventService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultFollowRequestService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultInstanceService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultListService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultMarkerService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultMediaService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultNotificationService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultPhotoAlbumService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultPhotoService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultPollService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultPushService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultReportService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultSearchService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultStatusService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultTagsService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultTimelineService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultTrendService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultUserService
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
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindInstance
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal data class ServiceCreationArgs(val baseUrl: String, val client: HttpClient)

val apiModule =
    DI.Module("ApiModule") {
        bindInstance<HttpClientEngine> {
            provideHttpClientEngine()
        }
        bindSingleton<Json> {
            JsonSerializer
        }
        bindSingleton<ServiceProvider>(tag = "default") {
            DefaultServiceProvider(
                factory = instance(),
                appInfoRepository = instance(),
            )
        }
        bindSingleton<ServiceProvider>(tag = "other") {
            DefaultServiceProvider(
                factory = instance(),
                appInfoRepository = instance(),
            )
        }
        bindFactory<ServiceCreationArgs, AnnouncementService> { arg ->
            DefaultAnnouncementService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, AppService> { arg ->
            DefaultAppService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, DirectMessageService> { arg ->
            DefaultDirectMessageService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, EventService> { arg ->
            DefaultEventService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, FollowRequestService> { arg ->
            DefaultFollowRequestService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, InstanceService> { arg ->
            DefaultInstanceService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, ListService> { arg ->
            DefaultListService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, MarkerService> { arg ->
            DefaultMarkerService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, MediaService> { arg ->
            DefaultMediaService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, NotificationService> { arg ->
            DefaultNotificationService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, PhotoAlbumService> { arg ->
            DefaultPhotoAlbumService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, PhotoService> { arg ->
            DefaultPhotoService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, PollService> { arg ->
            DefaultPollService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, PushService> { arg ->
            DefaultPushService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, ReportService> { arg ->
            DefaultReportService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, SearchService> { arg ->
            DefaultSearchService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, StatusService> { arg ->
            DefaultStatusService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, TagsService> { arg ->
            DefaultTagsService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, TimelineService> { arg ->
            DefaultTimelineService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, TrendsService> { arg ->
            DefaultTrendService(baseUrl = arg.baseUrl, client = arg.client)
        }
        bindFactory<ServiceCreationArgs, UserService> { arg ->
            DefaultUserService(baseUrl = arg.baseUrl, client = arg.client)
        }
    }

private val JsonSerializer = Json { ignoreUnknownKeys = true }
