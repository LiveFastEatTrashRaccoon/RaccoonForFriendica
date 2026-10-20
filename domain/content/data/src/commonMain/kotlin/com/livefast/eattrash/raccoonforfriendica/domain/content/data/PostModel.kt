package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashParams
import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import kotlin.jvm.Transient
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

data class PostModel(
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
    val inReplyTo: PostModel? = null,
    val lang: String? = null,
    @Transient val loadMoreButtonVisible: Boolean = false,
    @Transient val loadMoreButtonLoading: Boolean = false,
    val mentions: List<UserModel> = emptyList(),
    val parentId: String? = null,
    val pinned: Boolean = false,
    val poll: PollModel? = null,
    val quoteCount: Int = 0,
    val quoted: PostModel? = null,
    val quoteStatus: QuoteStatus? = null,
    val quotePermission: QuotePermission? = null,
    val reblog: PostModel? = null,
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
    @Transient val translation: PostModel? = null,
    @Transient val translationLoading: Boolean = false,
    @Transient val translationProvider: String? = null,
    val actionPermissions: PostActions = PostActions(),
)

val PostModel.safeKey: String
    get() {
        fun StringBuilder.appendKeys(p: PostModel) {
            append(p.id)
        }
        return buildString {
            appendKeys(this@safeKey)
            reblog?.run {
                append("--")
                appendKeys(this@run)
            }
        }
    }

val PostModel.original: PostModel get() = reblog ?: this

val PostModel.urlsForPreload: List<String>
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

val PostModel.blurHashParamsForPreload: List<BlurHashParams>
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

val PostModel.isNsfw: Boolean get() = reblog?.sensitive ?: sensitive

val Duration.isOldPost: Boolean
    get() = this > 30.days

val PostModel.titleToDisplay: String?
    get() = translation?.title?.takeIf { isShowingTranslation } ?: title

val PostModel.spoilerToDisplay: String?
    get() = translation?.spoiler?.takeIf { isShowingTranslation } ?: spoiler

val PostModel.contentToDisplay: String
    get() = translation?.content?.takeIf { isShowingTranslation } ?: content

val PostModel.pollToDisplay: PollModel?
    get() = translation?.poll?.takeIf { isShowingTranslation } ?: poll

val PostModel.attachmentsToDisplay: List<AttachmentModel>
    get() = translation?.attachments?.takeIf { isShowingTranslation } ?: attachments

val PostModel.cardToDisplay: PreviewCardModel?
    get() = translation?.card?.takeIf { isShowingTranslation } ?: card

val PostModel.nodeName: String
    get() =
        original.creator
            ?.handle
            ?.nodeName
            .orEmpty()
