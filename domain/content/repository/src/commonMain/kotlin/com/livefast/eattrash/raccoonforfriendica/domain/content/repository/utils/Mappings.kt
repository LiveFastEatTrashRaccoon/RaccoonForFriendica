package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ContentVisibility
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Field
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaCircle
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaContact
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhotoAlbum
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPrivateMessage
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.HistoryItem
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Instance
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.InstanceRule
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
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusSource
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserList
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserListReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.parseDate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.HashtagHistoryItem
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
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
        card = card?.toModel(),
        created = createdAt,
        creator = account?.toModel(),
        favorite = favourited,
        favoriteCount = favoritesCount,
        lang = lang,
        parentId = inReplyToId,
        pinned = pinned,
        reblogCount = reblogsCount,
        reblogged = reblogged,
        replyCount = repliesCount,
        // needed because, for compatibility, Friendica titles are replicated as spoilers on Mastodon
        spoiler = spoiler.takeIf { it != addons?.title },
        tags = tags.map { it.toModel() },
        updated = editedAt,
        url = url,
        visibility = visibility.toVisibility(),
        title = addons?.title?.takeIf { it.isNotBlank() },
        poll = poll?.toModel(),
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
        id = id,
        description = description,
        url = url,
        previewUrl = previewUrl,
        type = type.toModel(),
        blurHash = blurHash,
        originalWidth = meta?.original?.width,
        originalHeight = meta?.original?.height,
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
        discoverable = discoverable ?: true,
        noIndex = noIndex,
        url = url,
    )

internal fun CredentialAccount.toModel() =
    UserModel(
        avatar = avatar,
        bio = source?.note ?: note,
        bot = bot,
        created = createdAt,
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
        username = username,
        discoverable = discoverable ?: true,
        noIndex = noIndex,
        url = url,
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
        url = url.orEmpty(),
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
        thumbnail = thumb,
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
        name = title,
        id = id,
        exclusive = exclusive,
        replyPolicy = repliesPolicy?.toModel() ?: CircleReplyPolicy.List,
    )

internal fun FriendicaCircle.toModel() =
    CircleModel(
        name = title,
        id = id,
    )

internal fun Poll.toModel() =
    PollModel(
        id = id,
        expiresAt = expiresAt,
        expired = expired,
        multiple = multiple,
        votes = votesCount ?: 0,
        voters = votersCount ?: 0,
        options = options.map { it.toModel() },
        voted = voted,
        ownVotes = ownVotes,
    )

internal fun PollOption.toModel() =
    PollOptionModel(
        title = title,
        votes = votesCount,
    )

internal fun Instance.toModel() =
    NodeInfoModel(
        uri = uri,
        title = title,
        domain = domain,
        version = version,
        sourceUrl = sourceUrl,
        description = description,
        activeUsers = usage?.users?.activeMonth,
        thumbnail = thumbnail,
        languages = languages,
        rules = rules.map { it.toModel() },
        contact = contactAccount?.toModel(),
        characterLimit = configuration?.statuses?.maxCharacters,
        attachmentLimit = configuration?.statuses?.maxMediaAttachments,
    )

internal fun InstanceRule.toModel() =
    RuleModel(
        id = id,
        text = text,
    )

internal fun FriendicaContact.toModel() =
    UserModel(
        id = id.toString(),
        displayName = screenName,
        username = name,
        url = url,
        avatar = profileImageUrl,
        bio = description,
    )

internal fun FriendicaPrivateMessage.toModel() =
    DirectMessageModel(
        id = id.toString(),
        created =
            createdAt?.let { date ->
                parseDate(
                    value = date,
                    format = FriendicaDateFormats.PRIVATE_MESSAGES,
                )
            },
        text = text,
        title = title,
        sender = sender?.toModel(),
        recipient = recipient?.toModel(),
        parentUri = parentUri,
        read = seen ?: true,
    )

internal fun FriendicaPhotoAlbum.toModel() =
    MediaAlbumModel(
        name = name,
        created =
            created?.let { date ->
                parseDate(
                    value = date,
                    format = FriendicaDateFormats.PHOTO_ALBUMS,
                )
            },
        items = count,
    )

internal fun SearchResultType.toDto(): String =
    when (this) {
        SearchResultType.Entries -> "statuses"
        SearchResultType.Hashtags -> "hashtags"
        SearchResultType.Users -> "accounts"
    }

internal fun ScheduledStatus.toModel() =
    TimelineEntryModel(
        id = id,
        content = params?.text.orEmpty(),
        scheduled = scheduledAt,
        attachments = attachments.map { it.toModel() },
        parentId = params?.inReplyToId,
        sensitive = params?.sensitive ?: false,
        spoiler = params?.spoilerText,
        visibility = params?.visibility?.toVisibility() ?: Visibility.Public,
        poll = params?.poll?.toModel(),
    )

internal fun ReportCategory.toDto(): String =
    when (this) {
        ReportCategory.Legal -> "legal"
        ReportCategory.Other -> "other"
        ReportCategory.Spam -> "spam"
        ReportCategory.Violation -> "violation"
    }

private object FriendicaDateFormats {
    const val PRIVATE_MESSAGES = "EEE MMM dd HH:mm:ss xxxx yyyy"
    const val PHOTO_ALBUMS = "yyyy-MM-dd HH:mm:ss"
}
