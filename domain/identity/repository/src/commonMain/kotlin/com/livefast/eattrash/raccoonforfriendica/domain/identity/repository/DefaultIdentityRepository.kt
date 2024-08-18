package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultIdentityRepository(
    private val accountRepository: AccountRepository,
    private val provider: ServiceProvider,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IdentityRepository,
    MutableIdentityRepository {
    override val isLogged = MutableStateFlow(false)
    override val currentUser = MutableStateFlow<UserModel?>(null)

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun changeIsLogged(value: Boolean) {
        isLogged.update { value }
        if (value) {
            refreshUser()
        } else {
            currentUser.update { null }
        }
    }

    private fun refreshUser() {
        scope.launch {
            val handle = accountRepository.getActive()?.handle.orEmpty()
            if (handle.isEmpty()) {
                currentUser.update { null }
                return@launch
            }

            try {
                val user = provider.users.lookup(handle)
                currentUser.update {
                    UserModel(
                        id = user.id,
                        handle = user.acct,
                        username = user.username,
                    )
                }
            } catch (e: Throwable) {
                currentUser.update { null }
            }
        }
    }
}
