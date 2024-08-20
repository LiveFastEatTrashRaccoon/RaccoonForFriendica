package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ContentVisibility
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Field
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaCircle
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaContact
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.HistoryItem
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaAttachment
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.AUDIO
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.GIFV
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.IMAGE
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.UNKNOWN
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.VIDEO
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Notification
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusContext
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.HashtagHistoryItem
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType as MediaTypeDto
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.NotificationType as NotificationTypeDto

internal fun Status.toModelWithReply() =
    toModel().copy(
        inReplyTo = inReplyToStatus?.toModel(),
        reblog = reblog?.toModel(),
    )

internal fun Status.toModel() =
    TimelineEntryModel(
        attachments = attachments.map { it.toModel() },
        id = id,
        content = content,
        sensitive = sensitive,
        bookmarked = bookmarked,
        created = createdAt,
        creator = account?.toModel(),
        edited = editedAt,
        favorite = favourited,
        favoriteCount = favoritesCount,
        lang = lang,
        parentId = inReplyToId,
        pinned = pinned,
        reblogCount = reblogsCount,
        reblogged = reblogged,
        replyCount = repliesCount,
        spoiler = spoiler,
        tags = tags.map { it.toModel() },
        updated = editedAt,
        url = url,
        visibility = visibility.toModel(),
        title = addons?.title?.takeIf { it.isNotBlank() },
    )

internal fun StatusContext.toModel() =
    TimelineContextModel(
        ancestors = ancestors.map { it.toModelWithReply() },
        descendants = descendants.map { it.toModelWithReply() },
    )

internal fun MediaAttachment.toModel() =
    AttachmentModel(
        id = id,
        description = description,
        url = url,
        previewUrl = previewUrl,
        type = type.toModel(),
    )

internal fun MediaTypeDto.toModel(): MediaType =
    when (this) {
        IMAGE -> MediaType.Image
        VIDEO -> MediaType.Video
        GIFV -> MediaType.Image
        AUDIO -> MediaType.Audio
        UNKNOWN -> MediaType.Unknown
    }

internal fun ContentVisibility.toModel(): Visibility =
    when (this) {
        ContentVisibility.PUBLIC -> Visibility.Public
        ContentVisibility.UNLISTED -> Visibility.Unlisted
        ContentVisibility.PRIVATE -> Visibility.Private
        ContentVisibility.DIRECT -> Visibility.Direct
    }

internal fun Visibility.toDto(): ContentVisibility =
    when (this) {
        Visibility.Direct -> ContentVisibility.DIRECT
        Visibility.Private -> ContentVisibility.PRIVATE
        Visibility.Public -> ContentVisibility.PUBLIC
        Visibility.Unlisted -> ContentVisibility.UNLISTED
    }

internal fun Account.toModel() =
    UserModel(
        avatar = avatar,
        bio = note,
        bot = bot,
        created = createdAt,
        displayName = displayName,
        entryCount = statusesCount,
        fields = fields.map { it.toModel() },
        followers = followersCount,
        following = followingCount,
        group = group,
        handle = acct,
        header = header,
        id = id,
        locked = locked,
        username = username,
    )

internal fun Field.toModel() =
    FieldModel(
        key = name,
        value = value,
        verified = verifiedAt != null,
    )

internal fun HistoryItem.toModel() =
    HashtagHistoryItem(
        day = day,
        uses = uses,
        users = accounts,
    )

internal fun Tag.toModel() =
    TagModel(
        url = url,
        name = name,
        following = following,
        history = history?.map { it.toModel() }.orEmpty(),
    )

internal fun NotificationType.toDto(): NotificationTypeDto? =
    when (this) {
        NotificationType.Entry -> NotificationTypeDto.STATUS
        NotificationType.Favorite -> NotificationTypeDto.FAVOURITE
        NotificationType.Follow -> NotificationTypeDto.FOLLOW
        NotificationType.FollowRequest -> NotificationTypeDto.FOLLOW_REQUEST
        NotificationType.Mention -> NotificationTypeDto.MENTION
        NotificationType.Poll -> NotificationTypeDto.POLL
        NotificationType.Reblog -> NotificationTypeDto.REBLOG
        NotificationType.Update -> NotificationTypeDto.UPDATE
        else -> null
    }

internal fun NotificationTypeDto.toModel(): NotificationType =
    when (this) {
        NotificationTypeDto.MENTION -> NotificationType.Mention
        NotificationTypeDto.STATUS -> NotificationType.Entry
        NotificationTypeDto.REBLOG -> NotificationType.Reblog
        NotificationTypeDto.FOLLOW -> NotificationType.Follow
        NotificationTypeDto.FOLLOW_REQUEST -> NotificationType.FollowRequest
        NotificationTypeDto.FAVOURITE -> NotificationType.Favorite
        NotificationTypeDto.POLL -> NotificationType.Poll
        NotificationTypeDto.UPDATE -> NotificationType.Update
        else -> NotificationType.Unknown
    }

internal fun Notification.toModel() =
    NotificationModel(
        id = id,
        type = type.toModel(),
        read = dismissed,
        user = account?.toModel(),
        entry = status?.toModelWithReply(),
    )

internal fun Relationship.toModel() =
    RelationshipModel(
        id = id,
        following = following,
        notifying = notifying,
        followedBy = followedBy,
        blocking = blocking,
        muting = muting,
        mutingNotifications = mutingNotifications,
        requested = requested,
        requestedBy = requestedBy,
        note = note,
    )

internal fun TrendsLink.toModel() =
    LinkModel(
        url = url,
        title = title.orEmpty(),
        authorName = authorName,
        description = description,
        image = image,
    )

internal fun FriendicaPhoto.toModel() =
    AttachmentModel(
        id = id,
        mediaId = mediaId.orEmpty(),
        url = link.firstOrNull().orEmpty(),
        type = MediaType.Image,
        description = desc,
        album = album,
    )

internal fun FriendicaCircle.toModel() =
    CircleModel(
        name = name,
        id = "$id",
        users = users.map { it.toModel() },
    )

internal fun FriendicaContact.toModel() =
    UserModel(
        avatar = avatar,
        bio = note,
        created = createdAt,
        displayName = displayName,
        entryCount = statusesCount,
        followers = followersCount,
        id = id,
        locked = locked,
        username = username,
    )
