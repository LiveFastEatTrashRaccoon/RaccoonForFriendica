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

    override fun <T : Any> create(clazz: KClass<T>, args: ServiceCreationArgs): T {
        val service = when (clazz) {
            AnnouncementService::class -> announcementServiceFactory.create(args)
            AppService::class -> appServiceFactory.create(args)
            CollectionService::class -> collectionServiceFactory.create(args)
            DirectMessageService::class -> directMessageServiceFactory.create(args)
            EventService::class -> eventServiceFactory.create(args)
            FollowRequestService::class -> followRequestServiceFactory.create(args)
            InstanceService::class -> instanceServiceFactory.create(args)
            ListService::class -> listServiceFactory.create(args)
            MarkerService::class -> markerServiceFactory.create(args)
            MediaService::class -> mediaServiceFactory.create(args)
            NotificationService::class -> notificationServiceFactory.create(args)
            PhotoAlbumService::class -> photoAlbumServiceFactory.create(args)
            PhotoService::class -> photoServiceFactory.create(args)
            PollService::class -> pollServiceFactory.create(args)
            PushService::class -> pushServiceFactory.create(args)
            ReportService::class -> reportServiceFactory.create(args)
            SearchService::class -> searchServiceFactory.create(args)
            StatusService::class -> statusServiceFactory.create(args)
            TagsService::class -> tagsServiceFactory.create(args)
            TimelineService::class -> timelineServiceFactory.create(args)
            TrendsService::class -> trendsServiceFactory.create(args)
            UserService::class -> userServiceFactory.create(args)
            else -> throw IllegalArgumentException("Unknown service class: ${clazz.simpleName}")
        }
        return clazz.cast(service)
    }
}
