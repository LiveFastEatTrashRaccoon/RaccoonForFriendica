package com.livefast.eattrash.raccoonforfriendica.core.api.provider

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

interface ServiceProvider {
    val currentNode: String
    val announcement: AnnouncementService
    val app: AppService
    val directMessage: DirectMessageService
    val event: EventService
    val followRequest: FollowRequestService
    val instance: InstanceService
    val list: ListService
    val marker: MarkerService
    val media: MediaService
    val notification: NotificationService
    val photo: PhotoService
    val photoAlbum: PhotoAlbumService
    val poll: PollService
    val push: PushService
    val report: ReportService
    val search: SearchService
    val status: StatusService
    val tag: TagsService
    val timeline: TimelineService
    val translation: InnerTranslationService
    val trend: TrendsService
    val user: UserService

    fun changeNode(value: String)

    fun setAuth(credentials: ServiceCredentials? = null)
}
