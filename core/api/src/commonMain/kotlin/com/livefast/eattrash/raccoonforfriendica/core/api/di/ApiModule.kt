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
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DefaultInnerTranslationService
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
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.singleton

internal data class ServiceCreationArgs(val baseUrl: String, val client: HttpClient)

val apiModule =
    DI.Module("ApiModule") {
        bind<HttpClientEngine> {
            instance(provideHttpClientEngine())
        }
        bind<Json> {
            singleton { JsonSerializer }
        }
        bind<ServiceProvider>(tag = "default") {
            singleton {
                DefaultServiceProvider(
                    factory = instance(),
                    appInfoRepository = instance(),
                )
            }
        }
        bind<ServiceProvider>(tag = "other") {
            singleton {
                DefaultServiceProvider(
                    factory = instance(),
                    appInfoRepository = instance(),
                )
            }
        }
        bind<AnnouncementService> {
            factory { arg: ServiceCreationArgs ->
                DefaultAnnouncementService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<AppService> {
            factory { arg: ServiceCreationArgs ->
                DefaultAppService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<DirectMessageService> {
            factory { arg: ServiceCreationArgs ->
                DefaultDirectMessageService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<EventService> {
            factory { arg: ServiceCreationArgs ->
                DefaultEventService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<FollowRequestService> {
            factory { arg: ServiceCreationArgs ->
                DefaultFollowRequestService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<InnerTranslationService> {
            factory { arg: ServiceCreationArgs ->
                DefaultInnerTranslationService(baseUrl = arg.baseUrl, client = arg.client, json = instance())
            }
        }
        bind<InstanceService> {
            factory { arg: ServiceCreationArgs ->
                DefaultInstanceService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<ListService> {
            factory { arg: ServiceCreationArgs ->
                DefaultListService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<MarkerService> {
            factory { arg: ServiceCreationArgs ->
                DefaultMarkerService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<MediaService> {
            factory { arg: ServiceCreationArgs ->
                DefaultMediaService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<NotificationService> {
            factory { arg: ServiceCreationArgs ->
                DefaultNotificationService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<PhotoAlbumService> {
            factory { arg: ServiceCreationArgs ->
                DefaultPhotoAlbumService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<PhotoService> {
            factory { arg: ServiceCreationArgs ->
                DefaultPhotoService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<PollService> {
            factory { arg: ServiceCreationArgs ->
                DefaultPollService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<PushService> {
            factory { arg: ServiceCreationArgs ->
                DefaultPushService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<ReportService> {
            factory { arg: ServiceCreationArgs ->
                DefaultReportService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<SearchService> {
            factory { arg: ServiceCreationArgs ->
                DefaultSearchService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<StatusService> {
            factory { arg: ServiceCreationArgs ->
                DefaultStatusService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<TagsService> {
            factory { arg: ServiceCreationArgs ->
                DefaultTagsService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<TimelineService> {
            factory { arg: ServiceCreationArgs ->
                DefaultTimelineService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<TrendsService> {
            factory { arg: ServiceCreationArgs ->
                DefaultTrendService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
        bind<UserService> {
            factory { arg: ServiceCreationArgs ->
                DefaultUserService(baseUrl = arg.baseUrl, client = arg.client)
            }
        }
    }

private val JsonSerializer = Json { ignoreUnknownKeys = true }
