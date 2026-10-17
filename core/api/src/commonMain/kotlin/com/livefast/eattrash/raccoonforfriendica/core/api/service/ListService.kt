package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaCircle
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Page
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserList
import com.livefast.eattrash.raccoonforfriendica.core.api.form.EditListForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.EditListMembersForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted

interface ListService {
    suspend fun getAll(): List<UserList>

    suspend fun getFriendicaCircles(): List<FriendicaCircle>

    suspend fun getBy(id: String): UserList

    suspend fun getMembers(id: String, maxId: String? = null, limit: Int = 20): Page<Account>

    suspend fun create(data: EditListForm): UserList

    suspend fun update(id: String, data: EditListForm): UserList

    suspend fun delete(id: String): Boolean

    suspend fun addMembers(id: String, data: EditListMembersForm): Boolean

    suspend fun removeMembers(id: String, data: EditListMembersForm): Boolean
}

fun interface ListServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): ListService
}
