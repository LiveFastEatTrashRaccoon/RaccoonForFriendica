package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListMembersForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultCirclesRepository(private val provider: ServiceProvider) : CirclesRepository {
    override suspend fun getAll(): List<CircleModel>? = withContext(Dispatchers.IO) {
        runCatching {
            provider.lists.getAll().map { it.toModel() }
        }.getOrNull()
    }

    override suspend fun get(id: String): CircleModel? = withContext(Dispatchers.IO) {
        runCatching {
            provider.lists.getBy(id).toModel()
        }.getOrNull()
    }

    override suspend fun getMembers(id: String, pageCursor: String?): List<UserModel>? = withContext(Dispatchers.IO) {
        runCatching {
            provider.lists
                .getMembers(
                    id = id,
                    maxId = pageCursor,
                ).map { it.toModel() }
        }.getOrNull()
    }

    override suspend fun create(title: String, replyPolicy: CircleReplyPolicy, exclusive: Boolean): CircleModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                val data =
                    EditListForm(
                        title = title,
                        replyPolicy = replyPolicy.toDto(),
                        exclusive = exclusive,
                    )
                provider.lists.create(data).toModel()
            }.getOrNull()
        }

    override suspend fun update(
        id: String,
        title: String,
        replyPolicy: CircleReplyPolicy,
        exclusive: Boolean,
    ): CircleModel? = withContext(Dispatchers.IO) {
        runCatching {
            val data =
                EditListForm(
                    title = title,
                    replyPolicy = replyPolicy.toDto(),
                    exclusive = exclusive,
                )
            provider.lists
                .update(
                    id = id,
                    data = data,
                ).toModel()
        }.getOrNull()
    }

    override suspend fun delete(id: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val res = provider.lists.delete(id)
            res.isSuccessful
        }.getOrElse { false }
    }

    override suspend fun addMembers(id: String, userIds: List<String>): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val data = EditListMembersForm(accountIds = userIds)
            val res = provider.lists.addMembers(id = id, data = data)
            res.isSuccessful
        }.getOrElse { false }
    }

    override suspend fun removeMembers(id: String, userIds: List<String>): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val data = EditListMembersForm(accountIds = userIds)
            val res = provider.lists.removeMembers(id = id, data = data)
            res.isSuccessful
        }.getOrElse { false }
    }
}
