package com.github.akesiseli.raccoonforfriendica.domain.content.repository

import com.github.akesiseli.raccoonforfriendica.core.api.dto.Account
import com.github.akesiseli.raccoonforfriendica.core.api.dto.ContentVisibility
import com.github.akesiseli.raccoonforfriendica.core.api.dto.Field
import com.github.akesiseli.raccoonforfriendica.core.api.dto.MediaAttachment
import com.github.akesiseli.raccoonforfriendica.core.api.dto.MediaType.AUDIO
import com.github.akesiseli.raccoonforfriendica.core.api.dto.MediaType.GIFV
import com.github.akesiseli.raccoonforfriendica.core.api.dto.MediaType.IMAGE
import com.github.akesiseli.raccoonforfriendica.core.api.dto.MediaType.UNKNOWN
import com.github.akesiseli.raccoonforfriendica.core.api.dto.MediaType.VIDEO
import com.github.akesiseli.raccoonforfriendica.core.api.dto.Status
import com.github.akesiseli.raccoonforfriendica.core.api.dto.Tag
import com.github.akesiseli.raccoonforfriendica.domain.content.data.AccountModel
import com.github.akesiseli.raccoonforfriendica.domain.content.data.AttachmentModel
import com.github.akesiseli.raccoonforfriendica.domain.content.data.FieldModel
import com.github.akesiseli.raccoonforfriendica.domain.content.data.MediaType
import com.github.akesiseli.raccoonforfriendica.domain.content.data.TagModel
import com.github.akesiseli.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.github.akesiseli.raccoonforfriendica.domain.content.data.Visibility
import com.github.akesiseli.raccoonforfriendica.core.api.dto.MediaType as MediaTypeDto

fun Status.toModel() =
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
    )

fun MediaAttachment.toModel() =
    AttachmentModel(
        id = id,
        description = description,
        url = url,
        previewUrl = previewUrl,
        type = type.toModel(),
    )

fun MediaTypeDto.toModel(): MediaType =
    when (this) {
        IMAGE -> MediaType.Image
        VIDEO -> MediaType.Video
        GIFV -> MediaType.Image
        AUDIO -> MediaType.Audio
        UNKNOWN -> MediaType.Unknown
    }

fun ContentVisibility.toModel(): Visibility =
    when (this) {
        ContentVisibility.PUBLIC -> Visibility.Public
        ContentVisibility.UNLISTED -> Visibility.Unlisted
        ContentVisibility.PRIVATE -> Visibility.Private
        ContentVisibility.DIRECT -> Visibility.Direct
    }

fun Account.toModel() =
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

fun Field.toModel() =
    FieldModel(
        key = name,
        value = value,
    )

fun Tag.toModel() =
    TagModel(
        url = url,
        name = name,
    )
