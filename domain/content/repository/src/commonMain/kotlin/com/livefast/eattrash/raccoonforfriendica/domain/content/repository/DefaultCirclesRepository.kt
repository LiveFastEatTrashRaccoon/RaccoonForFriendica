package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListMembersForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.utils.io.CancellationException

internal class DefaultCirclesRepository(private val provider: ServiceProvider) : CirclesRepository {
    override suspend fun getAll(): List<CircleModel>? = try {
        provider.list.getAll().map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun get(id: String): CircleModel? = try {
        provider.list.getBy(id).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getMembers(id: String, pageCursor: String?): List<UserModel>? = try {
        provider.list.getMembers(id = id, maxId = pageCursor).map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun create(title: String, replyPolicy: CircleReplyPolicy, exclusive: Boolean): CircleModel? = try {
        val data =
            EditListForm(
                title = title,
                replyPolicy = replyPolicy.toDto(),
                exclusive = exclusive,
            )
        provider.list.create(data).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun update(
        id: String,
        title: String,
        replyPolicy: CircleReplyPolicy,
        exclusive: Boolean,
    ): CircleModel? = try {
        val data =
            EditListForm(
                title = title,
                replyPolicy = replyPolicy.toDto(),
                exclusive = exclusive,
            )
        provider.list.update(id = id, data = data).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun delete(id: String): Boolean = provider.list.delete(id)

    override suspend fun addMembers(id: String, userIds: List<String>): Boolean {
        val data = EditListMembersForm(accountIds = userIds)
        return provider.list.addMembers(id = id, data = data)
    }

    override suspend fun removeMembers(id: String, userIds: List<String>): Boolean {
        val data = EditListMembersForm(accountIds = userIds)
        return provider.list.removeMembers(id = id, data = data)
    }
}
