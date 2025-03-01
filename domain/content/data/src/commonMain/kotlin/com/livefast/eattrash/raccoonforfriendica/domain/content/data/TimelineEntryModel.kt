package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashParams
import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import kotlin.jvm.Transient
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

data class TimelineEntryModel(
    val attachments: List<AttachmentModel> = emptyList(),
    val bookmarked: Boolean = false,
    @Transient val bookmarkLoading: Boolean = false,
    val card: PreviewCardModel? = null,
    val content: String,
    val created: String? = null,
    val creator: UserModel? = null,
    @Transient val depth: Int = 0,
    val dislikesCount: Int = 0,
    val disliked: Boolean = false,
    val emojis: List<EmojiModel> = emptyList(),
    val favorite: Boolean = false,
    val favoriteCount: Int = 0,
    @Transient val favoriteLoading: Boolean = false,
    @Transient val dislikeLoading: Boolean = false,
    val id: String,
    val inReplyTo: TimelineEntryModel? = null,
    val lang: String? = null,
    @Transient val loadMoreButtonVisible: Boolean = false,
    @Transient val loadMoreButtonLoading: Boolean = false,
    val mentions: List<UserModel> = emptyList(),
    val parentId: String? = null,
    val pinned: Boolean = false,
    val poll: PollModel? = null,
    val reblog: TimelineEntryModel? = null,
    val reblogCount: Int = 0,
    @Transient val reblogLoading: Boolean = false,
    val reblogged: Boolean = false,
    val replyCount: Int = 0,
    val scheduled: String? = null,
    val sensitive: Boolean = false,
    val sourcePlatform: String? = null,
    val sourceProtocol: String? = null,
    val spoiler: String? = null,
    val tags: List<TagModel> = emptyList(),
    val title: String? = null,
    val updated: String? = null,
    val url: String? = null,
    val visibility: Visibility = Visibility.Public,
    val localOnly: Boolean = false,
    @Transient val isShowingTranslation: Boolean = false,
    @Transient val translation: TimelineEntryModel? = null,
    @Transient val translationLoading: Boolean = false,
    @Transient val translationProvider: String? = null,
    @Transient val foreign: Boolean = false,
)

val TimelineEntryModel.safeKey: String
    get() {
        fun StringBuilder.appendKeys(e: TimelineEntryModel) {
            append(e.id)
        }
        return buildString {
            appendKeys(this@safeKey)
            reblog?.run {
                append("--")
                appendKeys(this@run)
            }
        }
    }

val TimelineEntryModel.original: TimelineEntryModel get() = reblog ?: this

val TimelineEntryModel.urlsForPreload: List<String>
    get() =
        buildList {
            attachments
                .mapNotNull { attachment ->
                    if (attachment.type != MediaType.Image) {
                        null
                    } else {
                        attachment.url.takeUnless { it.isNotBlank() }
                    }
                }.also { urls ->
                    addAll(urls)
                }
            creator?.avatar?.takeUnless { it.isNotBlank() }?.also { add(it) }
            card?.image?.takeUnless { it.isNotBlank() }?.also { add(it) }
        }

val TimelineEntryModel.blurHashParamsForPreload: List<BlurHashParams>
    get() =
        buildList {
            attachments
                .mapNotNull { attachment ->
                    if (attachment.type != MediaType.Image) {
                        null
                    } else {
                        attachment.blurHash?.takeUnless { it.isNotBlank() }?.let {
                            BlurHashParams(
                                hash = it,
                                width = attachment.originalWidth ?: 0,
                                height = attachment.originalHeight ?: 0,
                            )
                        }
                    }
                }.also { hashes ->
                    addAll(hashes)
                }
        }

val TimelineEntryModel.isNsfw: Boolean get() = reblog?.sensitive ?: sensitive

val Duration.isOldEntry: Boolean
    get() = this > 30.days

val TimelineEntryModel.titleToDisplay: String?
    get() = translation?.title?.takeIf { isShowingTranslation } ?: title

val TimelineEntryModel.spoilerToDisplay: String?
    get() = translation?.spoiler?.takeIf { isShowingTranslation } ?: spoiler

val TimelineEntryModel.contentToDisplay: String
    get() = translation?.content?.takeIf { isShowingTranslation } ?: content

val TimelineEntryModel.pollToDisplay: PollModel?
    get() = translation?.poll?.takeIf { isShowingTranslation } ?: poll

val TimelineEntryModel.attachmentsToDisplay: List<AttachmentModel>
    get() = translation?.attachments?.takeIf { isShowingTranslation } ?: attachments

val TimelineEntryModel.cardToDisplay: PreviewCardModel?
    get() = translation?.card?.takeIf { isShowingTranslation } ?: card

val TimelineEntryModel.nodeName: String
    get() =
        original.creator
            ?.handle
            ?.nodeName
            .orEmpty()
