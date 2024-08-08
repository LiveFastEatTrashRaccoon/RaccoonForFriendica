package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel

interface AccountRepository {
    suspend fun getById(id: String): AccountModel?

    suspend fun getByHandle(handle: String): AccountModel?
}
