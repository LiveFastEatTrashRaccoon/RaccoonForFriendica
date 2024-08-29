package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import kotlinx.coroutines.flow.StateFlow

interface IdentityRepository {
    val currentUser: StateFlow<UserModel?>

    suspend fun refreshCurrentUser(userId: String?)
}
