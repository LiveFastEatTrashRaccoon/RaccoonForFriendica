package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class TimelineEntryModel(
    val attachments: List<AttachmentModel> = emptyList(),
    val bookmarked: Boolean = false,
    val content: String,
    val created: String? = null,
    val creator: AccountModel? = null,
    val favorite: Boolean = false,
    val favoriteCount: Int = 0,
    val id: String,
    val lang: String? = null,
    val parentId: String? = null,
    val pinned: Boolean = false,
    val reblogCount: Int = 0,
    val reblogged: Boolean = false,
    val replyCount: Int = 0,
    val sensitive: Boolean = false,
    val spoiler: String? = null,
    val tags: List<TagModel> = emptyList(),
    val updated: String? = null,
    val url: String? = null,
    val visibility: Visibility = Visibility.Public,
)
