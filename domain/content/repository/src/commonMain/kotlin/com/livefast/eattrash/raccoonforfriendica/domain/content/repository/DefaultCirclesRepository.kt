package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListMembersForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel

internal class DefaultCirclesRepository(private val provider: ServiceProvider) : CirclesRepository {
    override suspend fun getAll(): List<CircleModel>? = runCatching {
        provider.lists.getAll().map { it.toModel() }
    }.getOrNull()

    override suspend fun get(id: String): CircleModel? = runCatching {
        provider.lists.getBy(id).toModel()
    }.getOrNull()

    override suspend fun getMembers(id: String, pageCursor: String?): List<UserModel>? = runCatching {
        provider.lists.getMembers(id = id, maxId = pageCursor).map { it.toModel() }
    }.getOrNull()

    override suspend fun create(title: String, replyPolicy: CircleReplyPolicy, exclusive: Boolean): CircleModel? =

        runCatching {
            val data =
                EditListForm(
                    title = title,
                    replyPolicy = replyPolicy.toDto(),
                    exclusive = exclusive,
                )
            provider.lists.create(data).toModel()
        }.getOrNull()

    override suspend fun update(
        id: String,
        title: String,
        replyPolicy: CircleReplyPolicy,
        exclusive: Boolean,
    ): CircleModel? = runCatching {
        val data =
            EditListForm(
                title = title,
                replyPolicy = replyPolicy.toDto(),
                exclusive = exclusive,
            )
        provider.lists.update(id = id, data = data).toModel()
    }.getOrNull()

    override suspend fun delete(id: String): Boolean = provider.lists.delete(id)

    override suspend fun addMembers(id: String, userIds: List<String>): Boolean {
        val data = EditListMembersForm(accountIds = userIds)
        return provider.lists.addMembers(id = id, data = data)
    }

    override suspend fun removeMembers(id: String, userIds: List<String>): Boolean {
        val data = EditListMembersForm(accountIds = userIds)
        return provider.lists.removeMembers(id = id, data = data)
    }
}
