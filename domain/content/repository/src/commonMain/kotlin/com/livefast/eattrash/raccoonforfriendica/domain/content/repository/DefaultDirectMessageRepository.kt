package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters

internal class DefaultDirectMessageRepository(private val provider: ServiceProvider) : DirectMessageRepository {
    override suspend fun getAll(page: Int, limit: Int?): List<DirectMessageModel>? = runCatching {
        provider.directMessage
            .getAll(
                page = page,
                count = limit ?: DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    }.getOrNull()

    override suspend fun getReplies(parentUri: String, page: Int): List<DirectMessageModel>? = runCatching {
        provider.directMessage
            .getConversation(
                uri = parentUri,
                page = page,
                count = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    }.getOrNull()

    override suspend fun pollReplies(parentUri: String, minId: String): List<DirectMessageModel>? = runCatching {
        provider.directMessage
            .getConversation(
                uri = parentUri,
                sinceId = minId.toLongOrNull() ?: 0,
                page = 1,
                count = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    }.getOrNull()

    override suspend fun create(
        recipientId: String,
        text: String,
        title: String?,
        inReplyTo: String?,
    ): DirectMessageModel? = runCatching {
        val data =
            FormDataContent(
                formData =
                Parameters.build {
                    if (title != null) {
                        append("title", title)
                    }
                    append("text", text)
                    if (inReplyTo != null) {
                        append("replyto", inReplyTo)
                    }
                    append("user_id", recipientId)
                },
            )
        provider.directMessage.create(data).toModel()
    }.getOrNull()

    override suspend fun delete(id: String): Boolean = runCatching {
        val res = provider.directMessage.delete(id.toLong())
        res.result == RESULT_OK
    }.getOrElse { false }

    override suspend fun markAsRead(id: String): Boolean = runCatching {
        val res = provider.directMessage.markAsRead(id.toLong())
        res.result == RESULT_OK
    }.getOrElse { false }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
        private const val RESULT_OK = "ok"
    }
}
