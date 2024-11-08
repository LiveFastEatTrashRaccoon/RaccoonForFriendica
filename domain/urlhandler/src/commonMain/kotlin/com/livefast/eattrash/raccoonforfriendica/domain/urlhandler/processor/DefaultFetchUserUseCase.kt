package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.utils.detailName
import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import kotlinx.coroutines.withTimeoutOrNull

internal class DefaultFetchUserUseCase(
    private val userRepository: UserRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
) : FetchUserUseCase {
    override suspend fun invoke(handle: String): UserModel? =
        // wait at most USER_SEARCH_TIMEOUT_MILLIS failing if the request takes longer
        withTimeoutOrNull(USER_SEARCH_TIMEOUT_MILLIS) {
            userRepository.getByHandle(handle).takeIf {
                // eventual consistency check: the match should be exact
                val currentNode = apiConfigurationRepository.node.value
                val userNode = handle.nodeName
                if (currentNode == userNode) {
                    // local user
                    it?.handle == handle.detailName
                } else {
                    // federated user
                    it?.handle == handle
                }
            }
        }

    companion object {
        private const val USER_SEARCH_TIMEOUT_MILLIS = 1500L
    }
}
