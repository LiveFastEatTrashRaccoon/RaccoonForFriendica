package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Status(
    @SerialName("account") val account: Account? = null,
    @SerialName("bookmarked") val bookmarked: Boolean = false,
    @SerialName("card") val card: PreviewCard? = null,
    @SerialName("content") val content: String = "",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("edited_at") val editedAt: String? = null,
    @SerialName("emojis") val emojis: List<CustomEmoji>? = null,
    @SerialName("favourited") val favourited: Boolean = false,
    @SerialName("favourites_count") val favoritesCount: Int = 0,
    @SerialName("friendica") val addons: StatusAddons? = null,
    @SerialName("id") val id: String,
    @SerialName("in_reply_to_account_id") val inReplyToAccountId: String? = null,
    @SerialName("in_reply_to_id") val inReplyToId: String? = null,
    @SerialName("in_reply_to_status") val inReplyToStatus: Status? = null,
    @SerialName("language") val lang: String? = null,
    @SerialName("media_attachments") val attachments: List<MediaAttachment> = emptyList(),
    @SerialName("mentions") val mentions: List<StatusMention> = emptyList(),
    @SerialName("muted") val muted: Boolean = false,
    @SerialName("pinned") val pinned: Boolean = false,
    @SerialName("poll") val poll: Poll? = null,
    @SerialName("reblog") val reblog: Status? = null,
    @SerialName("reblogged") val reblogged: Boolean = false,
    @SerialName("reblogs_count") val reblogsCount: Int = 0,
    @SerialName("replies_count") val repliesCount: Int = 0,
    @SerialName("sensitive") val sensitive: Boolean = false,
    @SerialName("spoiler_text") val spoiler: String? = null,
    @SerialName("tags") val tags: List<Tag> = emptyList(),
    @SerialName("url") val url: String? = null,
    @SerialName("visibility") val visibility: String = ContentVisibility.PUBLIC,
    @SerialName("local_only") val localOnly: Boolean = false,
)
