package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListMembersForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaCircle
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserList

interface ListService {
    suspend fun getAll(): List<UserList>

    suspend fun getFriendicaCircles(): List<FriendicaCircle>

    suspend fun getBy(id: String): UserList

    suspend fun getMembers(id: String, maxId: String? = null, limit: Int = 20): List<Account>

    suspend fun create(data: EditListForm): UserList

    suspend fun update(id: String, data: EditListForm): UserList

    suspend fun delete(id: String): Boolean

    suspend fun addMembers(id: String, data: EditListMembersForm): Boolean

    suspend fun removeMembers(id: String, data: EditListMembersForm): Boolean
}
