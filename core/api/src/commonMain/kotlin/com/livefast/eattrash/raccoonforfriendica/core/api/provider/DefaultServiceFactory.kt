package com.livefast.eattrash.raccoonforfriendica.core.api.provider

import com.livefast.eattrash.raccoonforfriendica.core.api.service.AnnouncementService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.AnnouncementServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.AppService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.AppServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.CollectionService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.CollectionServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DirectMessageService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.DirectMessageServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.EventService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.EventServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.FollowRequestService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.FollowRequestServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.InstanceService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.InstanceServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.ListService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.ListServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.MarkerService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.MarkerServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.MediaService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.MediaServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.NotificationService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.NotificationServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PhotoAlbumService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PhotoAlbumServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PhotoService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PhotoServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PollService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PollServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PushService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PushServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.ReportService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.ReportServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.SearchService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.SearchServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.StatusService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.StatusServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TagsService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TagsServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TimelineService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TimelineServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TrendsService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TrendsServiceFactory
import com.livefast.eattrash.raccoonforfriendica.core.api.service.UserService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.UserServiceFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import kotlin.reflect.KClass
import kotlin.reflect.cast

@ContributesBinding(AppScope::class)
@Inject
class DefaultServiceFactory(
    private val announcementServiceFactory: AnnouncementServiceFactory,
    private val appServiceFactory: AppServiceFactory,
    private val collectionServiceFactory: CollectionServiceFactory,
    private val directMessageServiceFactory: DirectMessageServiceFactory,
    private val eventServiceFactory: EventServiceFactory,
    private val followRequestServiceFactory: FollowRequestServiceFactory,
    private val instanceServiceFactory: InstanceServiceFactory,
    private val listServiceFactory: ListServiceFactory,
    private val markerServiceFactory: MarkerServiceFactory,
    private val mediaServiceFactory: MediaServiceFactory,
    private val notificationServiceFactory: NotificationServiceFactory,
    private val photoAlbumServiceFactory: PhotoAlbumServiceFactory,
    private val photoServiceFactory: PhotoServiceFactory,
    private val pollServiceFactory: PollServiceFactory,
    private val pushServiceFactory: PushServiceFactory,
    private val reportServiceFactory: ReportServiceFactory,
    private val searchServiceFactory: SearchServiceFactory,
    private val statusServiceFactory: StatusServiceFactory,
    private val tagsServiceFactory: TagsServiceFactory,
    private val timelineServiceFactory: TimelineServiceFactory,
    private val trendsServiceFactory: TrendsServiceFactory,
    private val userServiceFactory: UserServiceFactory,
) : ServiceFactory {

    override fun <T : Any> create(clazz: KClass<T>, baseUrl: String, client: HttpClient): T {
        val service = when (clazz) {
            AnnouncementService::class -> announcementServiceFactory.create(baseUrl, client)
            AppService::class -> appServiceFactory.create(baseUrl, client)
            CollectionService::class -> collectionServiceFactory.create(baseUrl, client)
            DirectMessageService::class -> directMessageServiceFactory.create(baseUrl, client)
            EventService::class -> eventServiceFactory.create(baseUrl, client)
            FollowRequestService::class -> followRequestServiceFactory.create(baseUrl, client)
            InstanceService::class -> instanceServiceFactory.create(baseUrl, client)
            ListService::class -> listServiceFactory.create(baseUrl, client)
            MarkerService::class -> markerServiceFactory.create(baseUrl, client)
            MediaService::class -> mediaServiceFactory.create(baseUrl, client)
            NotificationService::class -> notificationServiceFactory.create(baseUrl, client)
            PhotoAlbumService::class -> photoAlbumServiceFactory.create(baseUrl, client)
            PhotoService::class -> photoServiceFactory.create(baseUrl, client)
            PollService::class -> pollServiceFactory.create(baseUrl, client)
            PushService::class -> pushServiceFactory.create(baseUrl, client)
            ReportService::class -> reportServiceFactory.create(baseUrl, client)
            SearchService::class -> searchServiceFactory.create(baseUrl, client)
            StatusService::class -> statusServiceFactory.create(baseUrl, client)
            TagsService::class -> tagsServiceFactory.create(baseUrl, client)
            TimelineService::class -> timelineServiceFactory.create(baseUrl, client)
            TrendsService::class -> trendsServiceFactory.create(baseUrl, client)
            UserService::class -> userServiceFactory.create(baseUrl, client)
            else -> throw IllegalArgumentException("Unknown service class: ${clazz.simpleName}")
        }
        return clazz.cast(service)
    }
}
