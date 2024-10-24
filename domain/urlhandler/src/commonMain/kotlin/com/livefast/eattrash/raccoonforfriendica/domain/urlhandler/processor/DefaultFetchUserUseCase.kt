package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import kotlinx.coroutines.withTimeoutOrNull

internal class DefaultFetchUserUseCase(
    private val userRepository: UserRepository,
) : FetchUserUseCase {
    override suspend fun invoke(handle: String): UserModel? =
        withTimeoutOrNull(USER_SEARCH_TIMEOUT_MILLIS) {
            userRepository.getByHandle(handle)
        }

    companion object {
        private const val USER_SEARCH_TIMEOUT_MILLIS = 1000L
    }
}
