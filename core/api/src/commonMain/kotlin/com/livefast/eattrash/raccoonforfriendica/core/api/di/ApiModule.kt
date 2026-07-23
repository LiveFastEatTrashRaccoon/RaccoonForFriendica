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
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal data class ServiceCreationArgs(val baseUrl: String, val client: HttpClient)

val apiModule = module {
    single<Json> {
        JsonSerializer
    }
    single<HttpClientEngine> {
        provideHttpClientEngine()
    }
    single<ServiceProvider>(named("default")) {
        DefaultServiceProvider(
            factory = get(),
            appInfoRepository = get(),
        )
    }
    factory<ServiceProvider>(named("other")) {
        DefaultServiceProvider(
            factory = get(),
            appInfoRepository = get(),
        )
    }
    factory<AnnouncementService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultAnnouncementService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<AppService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultAppService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<DirectMessageService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultDirectMessageService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<EventService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultEventService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<FollowRequestService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultFollowRequestService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<InstanceService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultInstanceService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<ListService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultListService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<MarkerService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultMarkerService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<MediaService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultMediaService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<NotificationService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultNotificationService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<PhotoAlbumService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultPhotoAlbumService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<PhotoService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultPhotoService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<PollService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultPollService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<PushService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultPushService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<ReportService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultReportService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<SearchService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultSearchService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<StatusService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultStatusService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<TagsService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultTagsService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<TimelineService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultTimelineService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<TrendsService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultTrendService(baseUrl = arg.baseUrl, client = arg.client)
    }
    factory<UserService> { params ->
        val arg: ServiceCreationArgs = params.get()
        DefaultUserService(baseUrl = arg.baseUrl, client = arg.client)
    }
}

private val JsonSerializer = Json { ignoreUnknownKeys = true }
