package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import kotlinx.coroutines.withTimeoutOrNull

internal class DefaultFetchUserUseCase(
    private val userRepository: UserRepository,
) : FetchUserUseCase {
    override suspend fun invoke(handle: String): UserModel? =
        // wait at most USER_SEARCH_TIMEOUT_MILLIS failing if the request takes longer
        withTimeoutOrNull(USER_SEARCH_TIMEOUT_MILLIS) {
            userRepository.getByHandle(handle).takeIf {
                // eventual consistency check: the match should be exact
                it?.handle == handle
            }
        }

    companion object {
        private const val USER_SEARCH_TIMEOUT_MILLIS = 1500L
    }
}
