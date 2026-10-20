package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.QuotePolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.PagedList

interface PostRepository {
    fun getCachedByUser(): List<PostModel>

    suspend fun getByUser(
        userId: String,
        pageCursor: String? = null,
        excludeReplies: Boolean = false,
        excludeReblogs: Boolean = false,
        pinned: Boolean = false,
        onlyMedia: Boolean = false,
        enableCache: Boolean = false,
        refresh: Boolean = false,
        otherInstance: String? = null,
    ): List<PostModel>?

    suspend fun getById(id: String, otherInstance: String? = null): PostModel?

    suspend fun getSource(id: String, otherInstance: String? = null): PostModel?

    suspend fun getContext(id: String, otherInstance: String? = null): TimelineContextModel?

    suspend fun reblog(id: String): PostModel?

    suspend fun unreblog(id: String): PostModel?

    suspend fun pin(id: String): PostModel?

    suspend fun unpin(id: String): PostModel?

    suspend fun favorite(id: String): PostModel?

    suspend fun unfavorite(id: String): PostModel?

    suspend fun bookmark(id: String): PostModel?

    suspend fun unbookmark(id: String): PostModel?

    suspend fun getFavorites(pageCursor: String? = null): PagedList<PostModel>?

    suspend fun getBookmarks(pageCursor: String? = null): PagedList<PostModel>?

    suspend fun getUsersWhoFavorited(
        id: String,
        pageCursor: String? = null,
        otherInstance: String? = null,
    ): PagedList<UserModel>?

    suspend fun getUsersWhoReblogged(
        id: String,
        pageCursor: String? = null,
        otherInstance: String? = null,
    ): PagedList<UserModel>?

    suspend fun create(
        localId: String,
        text: String,
        title: String? = null,
        spoilerText: String? = null,
        inReplyTo: String? = null,
        quoted: String? = null,
        quotePolicy: QuotePolicy? = null,
        sensitive: Boolean = false,
        mediaIds: List<String>? = null,
        visibility: Visibility,
        lang: String? = null,
        scheduled: String? = null,
        pollOptions: List<String>? = null,
        pollExpirationDate: String? = null,
        pollMultiple: Boolean? = null,
    ): PostModel?

    suspend fun update(
        id: String,
        text: String,
        title: String? = null,
        spoilerText: String? = null,
        inReplyTo: String? = null,
        quoted: String? = null,
        quotePolicy: QuotePolicy? = null,
        sensitive: Boolean = false,
        mediaIds: List<String>? = null,
        visibility: Visibility,
        lang: String? = null,
        pollOptions: List<String>? = null,
        pollExpirationDate: String? = null,
        pollMultiple: Boolean? = null,
    ): PostModel?

    suspend fun delete(id: String): Boolean

    suspend fun submitPoll(pollId: String, choices: List<Int>): PollModel?

    suspend fun dislike(id: String): Boolean

    suspend fun undislike(id: String): Boolean

    suspend fun getQuotes(id: String, pageCursor: String? = null, otherInstance: String? = null): PagedList<PostModel>?

    suspend fun revokeQuote(quotedId: String, quotingId: String): Boolean
}
