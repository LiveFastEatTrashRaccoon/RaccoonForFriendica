package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import kotlinx.coroutines.flow.StateFlow

interface IdentityRepository {
    val isLogged: StateFlow<Boolean>
    val currentUser: StateFlow<UserModel?>

    fun refreshCurrentUser()
}

internal interface MutableIdentityRepository : IdentityRepository {
    fun changeIsLogged(value: Boolean)
}
