package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ContentVisibility
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Field
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaAttachment
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.AUDIO
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.GIFV
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.IMAGE
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.UNKNOWN
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType.VIDEO
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaType as MediaTypeDto

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

internal fun Account.toModel() =
    AccountModel(
        avatar = avatar,
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
    )

internal fun Tag.toModel() =
    TagModel(
        url = url,
        name = name,
    )
