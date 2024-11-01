package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ContentVisibility
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CustomEmoji
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Event
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Field
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaCircle
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaContact
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhotoAlbum
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPrivateMessage
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.HistoryItem
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Instance
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.InstanceRule
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Markers
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaAttachment
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.AUDIO
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.GIFV
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.IMAGE
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.UNKNOWN
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.VIDEO
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Notification
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Poll
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.PollOption
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.PreviewCard
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.PreviewCardType
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ScheduledStatus
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusContext
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusMention
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusSource
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserList
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserListReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.extractDatePart
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.parseDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.toEpochMillis
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.HashtagHistoryItem
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollOptionModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PreviewCardModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PreviewType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ReportCategory
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType as MediaTypeDto
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.NotificationType as NotificationTypeDto

internal fun Status.toModelWithReply() =
    toModel().copy(
        inReplyTo =
            inReplyToStatus?.toModel() ?: inReplyToId?.let { parentId ->
                TimelineEntryModel(
                    id = parentId,
                    creator = inReplyToAccountId?.let { userId -> UserModel(id = userId) },
                    content = "",
                )
            },
        reblog =
            reblog?.let {
                it.toModel().copy(
                    inReplyTo =
                        it.inReplyToStatus?.toModel() ?: it.inReplyToId?.let { parentId ->
                            TimelineEntryModel(
                                id = parentId,
                                creator = inReplyToAccountId?.let { userId -> UserModel(id = userId) },
                                content = "",
                            )
                        },
                )
            },
    )

internal fun Status.toModel() =
    TimelineEntryModel(
        attachments = attachments.map { it.toModel() },
        // taking HTML content from Friendica-specific add-ons if available (with embedded images)
        content = addons?.content.takeIf { !it.isNullOrBlank() } ?: content,
        bookmarked = bookmarked,
        card = card?.toModel(),
        created = createdAt,
        creator = account?.toModel(),
        dislikesCount = addons?.dislikesCount ?: 0,
        emojis = emojis?.map { it.toModel() }.orEmpty(),
        favorite = favourited,
        favoriteCount = favoritesCount,
        id = id,
        lang = lang,
        mentions = mentions.map { it.toModel() },
        parentId = inReplyToId,
        pinned = pinned,
        poll = poll?.toModel(),
        reblogCount = reblogsCount,
        reblogged = reblogged,
        replyCount = repliesCount,
        sensitive = sensitive,
        sourcePlatform = addons?.platform,
        sourceProtocol = addons?.network,
        // needed because, for compatibility, Friendica titles are replicated as spoilers on Mastodon
        spoiler = spoiler.takeIf { it != addons?.title },
        tags = tags.map { it.toModel() },
        // taking content from Friendica-specific add-ons if available
        title = addons?.title.takeIf { !it.isNullOrBlank() },
        updated = editedAt,
        url = url,
        visibility = visibility.toVisibility(),
    )

private fun StatusMention.toModel() =
    UserModel(
        id = id,
        username = username,
        handle = acct,
        url = url,
    )

internal fun StatusSource.toModel() =
    TimelineEntryModel(
        content = text.orEmpty(),
        id = id,
        spoiler = spoilerText,
    )

internal fun PreviewCard.toModel() =
    PreviewCardModel(
        title = title,
        description = description,
        type =
            when (type) {
                PreviewCardType.LINK -> PreviewType.Link
                PreviewCardType.PHOTO -> PreviewType.Photo
                PreviewCardType.VIDEO -> PreviewType.Video
                else -> PreviewType.Unknown
            },
        url = url,
        image = image,
        providerName = providerName,
    )

internal fun StatusContext.toModel() =
    TimelineContextModel(
        ancestors = ancestors.map { it.toModelWithReply() },
        descendants = descendants.map { it.toModelWithReply() },
    )

internal fun MediaAttachment.toModel() =
    AttachmentModel(
        blurHash = blurHash,
        description = description,
        id = id,
        originalHeight = meta?.original?.height,
        originalWidth = meta?.original?.width,
        previewUrl = previewUrl,
        type = type.toModel(),
        url = url,
    )

internal fun MediaTypeDto.toModel(): MediaType =
    when (this) {
        IMAGE -> MediaType.Image
        VIDEO -> MediaType.Video
        GIFV -> MediaType.Image
        AUDIO -> MediaType.Audio
        UNKNOWN -> MediaType.Unknown
    }

internal fun String.toVisibility(): Visibility =
    when (this) {
        ContentVisibility.PUBLIC -> Visibility.Public
        ContentVisibility.UNLISTED -> Visibility.Unlisted
        ContentVisibility.PRIVATE -> Visibility.Private
        ContentVisibility.DIRECT -> Visibility.Direct
        else -> Visibility.Circle(id = this)
    }

internal fun Visibility.toDto(): String =
    when (this) {
        Visibility.Direct -> ContentVisibility.DIRECT
        Visibility.Private -> ContentVisibility.PRIVATE
        Visibility.Public -> ContentVisibility.PUBLIC
        Visibility.Unlisted -> ContentVisibility.UNLISTED
        is Visibility.Circle -> id.orEmpty()
    }

internal fun Account.toModel() =
    UserModel(
        avatar = avatar,
        bio = note,
        bot = bot,
        created = createdAt,
        discoverable = discoverable ?: true,
        displayName = displayName,
        emojis = emojis?.map { it.toModel() }.orEmpty(),
        entryCount = statusesCount,
        fields = fields.map { it.toModel() },
        followers = followersCount,
        following = followingCount,
        group = group,
        handle = acct,
        header = header,
        id = id,
        locked = locked,
        noIndex = noIndex,
        url = url,
        username = username,
    )

internal fun CredentialAccount.toModel() =
    UserModel(
        avatar = avatar,
        bio = source?.note ?: note,
        bot = bot,
        created = createdAt,
        discoverable = discoverable ?: true,
        displayName = displayName,
        entryCount = statusesCount,
        fields = (source?.fields ?: fields).map { it.toModel() },
        followers = followersCount,
        following = followingCount,
        group = group,
        handle = acct,
        header = header,
        id = id,
        locked = locked,
        noIndex = noIndex,
        url = url,
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
        users = accounts,
        uses = uses,
    )

internal fun Tag.toModel() =
    TagModel(
        history = history?.map { it.toModel() }.orEmpty(),
        following = following,
        name = name,
        url = url,
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
        entry = status?.toModelWithReply(),
        id = id,
        read = dismissed,
        type = type.toModel(),
        user = account?.toModel(),
    )

internal fun Relationship.toModel() =
    RelationshipModel(
        blocking = blocking,
        followedBy = followedBy,
        following = following,
        id = id,
        muting = muting,
        mutingNotifications = mutingNotifications,
        note = note,
        notifying = notifying,
        requested = requested,
        requestedBy = requestedBy,
    )

internal fun TrendsLink.toModel() =
    LinkModel(
        authorName = authorName,
        description = description,
        image = image,
        title = title.orEmpty(),
        url = url.orEmpty(),
    )

internal fun FriendicaPhoto.toModel() =
    AttachmentModel(
        album = album,
        description = desc,
        id = id,
        mediaId = mediaId.orEmpty(),
        thumbnail = thumb,
        type = MediaType.Image,
        url = link.firstOrNull().orEmpty(),
    )

internal fun UserListReplyPolicy.toModel(): CircleReplyPolicy =
    when (this) {
        UserListReplyPolicy.FOLLOWED -> CircleReplyPolicy.Followed
        UserListReplyPolicy.LIST -> CircleReplyPolicy.List
        UserListReplyPolicy.NONE -> CircleReplyPolicy.None
    }

internal fun CircleReplyPolicy.toDto(): String =
    when (this) {
        CircleReplyPolicy.Followed -> "followed"
        CircleReplyPolicy.List -> "list"
        CircleReplyPolicy.None -> "none"
    }

internal fun UserList.toModel() =
    CircleModel(
        exclusive = exclusive,
        id = id,
        name = title,
        replyPolicy = repliesPolicy?.toModel() ?: CircleReplyPolicy.List,
        type = id.toCircleType(),
    )

private fun String.toCircleType(): CircleType =
    when {
        startsWith("channel:") -> CircleType.Predefined
        startsWith("group:") -> CircleType.Group
        else -> CircleType.UserDefined
    }

internal fun FriendicaCircle.toModel() =
    CircleModel(
        id = id,
        name = title,
    )

internal fun Poll.toModel() =
    PollModel(
        expired = expired,
        expiresAt = expiresAt,
        id = id,
        multiple = multiple,
        options = options.map { it.toModel() },
        ownVotes = ownVotes,
        voted = voted,
        voters = votersCount ?: 0,
        votes = votesCount ?: 0,
    )

internal fun PollOption.toModel() =
    PollOptionModel(
        title = title,
        votes = votesCount,
    )

internal fun Instance.toModel() =
    NodeInfoModel(
        activeUsers = usage?.users?.activeMonth,
        attachmentLimit = configuration?.statuses?.maxMediaAttachments,
        characterLimit = configuration?.statuses?.maxCharacters,
        contact = contactAccount?.toModel(),
        description = description,
        domain = domain,
        languages = languages,
        rules = rules.map { it.toModel() },
        sourceUrl = sourceUrl,
        thumbnail = thumbnail,
        title = title,
        uri = uri,
        version = version,
    )

internal fun InstanceRule.toModel() =
    RuleModel(
        id = id,
        text = text,
    )

internal fun FriendicaContact.toModel() =
    UserModel(
        avatar = profileImageUrl,
        bio = description,
        displayName = screenName,
        id = id.toString(),
        url = url,
        username = name,
    )

internal fun FriendicaPrivateMessage.toModel() =
    DirectMessageModel(
        created =
            createdAt?.let { date ->
                parseDate(
                    value = date,
                    format = FriendicaDateFormats.PRIVATE_MESSAGES,
                )
            },
        id = id.toString(),
        parentUri = parentUri,
        read = seen ?: true,
        recipient = recipient?.toModel(),
        sender = sender?.toModel(),
        text = text,
        title = title,
    )

internal fun FriendicaPhotoAlbum.toModel() =
    MediaAlbumModel(
        created =
            created?.let { date ->
                parseDate(
                    value = date,
                    format = FriendicaDateFormats.PHOTO_ALBUMS,
                )
            },
        items = count,
        name = name,
    )

internal fun SearchResultType.toDto(): String =
    when (this) {
        SearchResultType.Entries -> "statuses"
        SearchResultType.Hashtags -> "hashtags"
        SearchResultType.Users -> "accounts"
    }

internal fun ScheduledStatus.toModel() =
    TimelineEntryModel(
        attachments = attachments.map { it.toModel() },
        content = params?.text.orEmpty(),
        id = id,
        parentId = params?.inReplyToId,
        poll = params?.poll?.toModel(),
        scheduled = scheduledAt,
        sensitive = params?.sensitive ?: false,
        spoiler = params?.spoilerText,
        visibility = params?.visibility?.toVisibility() ?: Visibility.Public,
    )

internal fun ReportCategory.toDto(): String =
    when (this) {
        ReportCategory.Legal -> "legal"
        ReportCategory.Other -> "other"
        ReportCategory.Spam -> "spam"
        ReportCategory.Violation -> "violation"
    }

internal fun CustomEmoji.toModel() =
    EmojiModel(
        url = url,
        code = shortCode,
        staticUrl = staticUrl,
        category = category,
        visibleInPicker = visibleInPicker,
    )

private object FriendicaDateFormats {
    const val PRIVATE_MESSAGES = "EEE MMM dd HH:mm:ss xxxx yyyy"
    const val PHOTO_ALBUMS = "yyyy-MM-dd HH:mm:ss"
}

internal fun MarkerType.toDto(): String =
    when (this) {
        MarkerType.Home -> "home"
        MarkerType.Notifications -> "notifications"
    }

internal fun Markers.toModel(): List<MarkerModel> =
    buildList {
        this@toModel.home?.lastReadId?.also { lastReadId ->
            this +=
                MarkerModel(
                    type = MarkerType.Home,
                    lastReadId = lastReadId,
                )
        }
        this@toModel.notifications?.lastReadId?.also { lastReadId ->
            this +=
                MarkerModel(
                    type = MarkerType.Notifications,
                    lastReadId = lastReadId,
                )
        }
    }

private fun String.toEventType(): EventType =
    when (this) {
        "birthday" -> EventType.Birthday
        else -> EventType.Event
    }

internal fun Event.toModel() =
    EventModel(
        id = id.toString(),
        uri = uri,
        title = name,
        description = description,
        startTime =
            parseDate(
                value = startTime,
                format = FriendicaDateFormats.PHOTO_ALBUMS,
            ),
        endTime =
            endTime?.let { date ->
                parseDate(
                    value = date,
                    format = FriendicaDateFormats.PHOTO_ALBUMS,
                ).takeIf {
                    val t = it.toEpochMillis()
                    val (y, _) = t.extractDatePart()
                    y > 1970
                }
            },
        type = type.toEventType(),
        ongoing = noFinish != 0,
        place = place,
    )
