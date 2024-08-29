package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class DefaultIdentityRepository(
    private val provider: ServiceProvider,
) : IdentityRepository {
    override val currentUser = MutableStateFlow<UserModel?>(null)

    override suspend fun refreshCurrentUser(userId: String?) {
        if (userId.isNullOrEmpty()) {
            currentUser.update { null }
        } else {
            try {
                val user = provider.users.getById(userId)
                currentUser.update {
                    UserModel(
                        id = user.id,
                        handle = user.acct,
                        username = user.username,
                        avatar = user.avatar,
                        displayName = user.displayName,
                    )
                }
            } catch (e: Throwable) {
                currentUser.update { null }
            }
        }
    }
}
